package com.redgear.brace.eval;

import com.redgear.brace.ast.*;
import com.redgear.brace.lex.Lexer;
import com.redgear.brace.lex.Token;
import com.redgear.brace.parse.Parser;
import com.redgear.brace.walk.Walker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by LordBlackHole on 2016-12-30.
 */
public class Eval implements Walker {

    private static final Logger log = LoggerFactory.getLogger(Eval.class);
    private File moduleFile;
    private Deque<Object> stack = new LinkedList<>();
    private LibraryScope libraryScope;
    private ModuleScope moduleScope;
    private LocalScope localScope;

    public Eval() {

        libraryScope = new LibraryScope();

        new CoreLibrary().buildLibrary(libraryScope);

        libraryScope.putMacroFunc("&&", (scope, args) -> {

            if(args.size() != 2)
                throw new RuntimeException("Wrong number of arguments for op '&&', found: " + args);

            Expression left = args.get(0);
            Expression right = args.get(1);

            walk(left);

            Object first = stack.pop();

            if(first == null || first == Boolean.FALSE) {
                return false;
            } else {
                walk(right);
                Object second = stack.pop();

                if(second == null || second == Boolean.FALSE){
                    return false;
                } else {
                    return second;
                }
            }

        });

        libraryScope.putMacroFunc("||", (scope, args) -> {

            if(args.size() != 2)
                throw new RuntimeException("Wrong number of arguments for op '||', found: " + args);

            Expression left = args.get(0);
            Expression right = args.get(1);

            walk(left);

            Object first = stack.pop();

            if(first == null || first == Boolean.FALSE) {
                walk(right);
                Object second = stack.pop();

                if(second == null || second == Boolean.FALSE){
                    return false;
                } else {
                    return second;
                }
            } else {
                return first;
            }

        });

        libraryScope.putMacroFunc("if", (scope, args) -> {
            int argSize = args.size();

            if(argSize != 2 && argSize != 3) {
                throw new RuntimeException("Wrong number of arguments for if statement! Must have 2 or 3, found: " + argSize);
            }

            walk(args.get(0));

            Object test = stack.pop();

            if (test != null && test != Boolean.FALSE) {
                walk(args.get(1));
                return stack.pop();
            } else if(argSize == 3) {
                walk(args.get(2));
                return stack.pop();
            } else {
                return null;
            }
        });
    }

    public ModuleScope evaluate(File file) {
        moduleFile = file;
        Module mod;

        try(FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            log.info("Reading file: {}", file);
            List<Token> tokens = new Lexer(bufferedReader).tokenize().collect(Collectors.toList());

            mod = new Parser(tokens.iterator()).readModule();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        walk(mod);

        return moduleScope;
    }

    @Override
    public void walk(Assignment assignment) {
        String var = assignment.getVar().getName();

        walk(assignment.getExp());

        Object result = stack.peek();

        log.info("{} = {}", var, result);

        if(var != null) {
            localScope.putValue(var, result);
        }
    }

    @Override
    public void walk(Literal literal) {
        stack.push(literal.getValue());
    }

    @Override
    public void walk(Module module) {
        moduleScope = new ModuleScope(module.getName(), libraryScope);
        libraryScope.putModule(moduleScope);

        localScope = new LocalScope(moduleScope);

        module.getExpressions().forEach(this::walk);
    }

    @Override
    public void walk(Variable variable) {
        stack.push(localScope.getValue(variable.getName()));
    }

    @Override
    public void walk(Call call) {
        walk(call.getMethod());

        Object func = stack.pop();

        try {
            if (func == null) {
                throw new RuntimeException("No such function: " + call.getName() + " " + call.getLocation().print());
            } else if (func instanceof MacroFunc) {
                callMacroFunc(call, (MacroFunc) func);
            } else if (func instanceof LibFunc) {
                callLibFunc(call, (LibFunc) func);
            } else if (func instanceof Func) {
                callFunction(call, (Func) func);
            } else {
                throw new RuntimeException("Not a function: " + call.getName() + " " + call.getLocation().print());
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage() + " " + call.getLocation().print(), e);
        }
    }

    @Override
    public void walk(Func func) {
        stack.push(func);
    }

    @Override
    public void walk(Import im) {

        if (!libraryScope.hasModule(im.getId())) {

            String fullPath = moduleFile.toPath().resolveSibling(im.getId()).toString();

            if (!libraryScope.hasModule(fullPath)) {
                File oldFile = moduleFile;
                ModuleScope oldScope = moduleScope;

                evaluate(new File(fullPath));

                moduleFile = oldFile;
                moduleScope = oldScope;
            }
        }

    }

    @Override
    public void walk(Export ex) {
        walk(ex.getExpression());
        moduleScope.putValue(ex.getName(), stack.pop());
    }

    public interface LibFunc extends BiFunction<Scope, List<Object>, Object> {


    }

    public interface MacroFunc extends BiFunction<Scope, List<Expression>, Object> {

    }

    private void runFunc(Consumer<Scope> func) {
        LocalScope outerScope = localScope;
        localScope = new LocalScope(localScope);
        Deque<Object> outerStack = stack;
        stack = new LinkedList<>();

        func.accept(localScope);

        localScope = outerScope;
        outerStack.push(stack.pop());
        stack = outerStack;
    }

    private void callLibFunc(Call call, LibFunc func) {
        int size = call.getArguments().size();

        call.getArguments().forEach(this::walk);

        List<Object> args = new LinkedList<>();

        for(int i = 0; i < size; i++) {
            args.add(0, stack.pop());
        }

        runFunc(scope -> stack.push(func.apply(localScope, args)));
    }

    private void callFunction(Call call, Func func) {
        int size = call.getArguments().size();

        runFunc(scope -> {

            call.getArguments().forEach(this::walk);

            for(int i = size - 1; i >= 0; i--) {
                scope.putValue(func.getArgs().get(i).getName(), stack.pop());
            }

            func.getStatements().forEach(this::walk);
        });
    }

    private void callMacroFunc(Call call, MacroFunc func) {
        runFunc(scope -> stack.push(func.apply(scope, call.getArguments())));
    }
}
