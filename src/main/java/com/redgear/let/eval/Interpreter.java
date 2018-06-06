package com.redgear.let.eval;

import com.redgear.let.antlr.LetLexer;
import com.redgear.let.antlr.LetParser;
import com.redgear.let.ast.*;
import com.redgear.let.ast.Module;
import com.redgear.let.eval.lib.CoreLibrary;
import com.redgear.let.eval.lib.ListLibrary;
import com.redgear.let.eval.lib.MapLibrary;
import com.redgear.let.eval.lib.StringLibrary;
import javaslang.API;
import javaslang.Predicates;
import javaslang.collection.List;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.Predicates.instanceOf;

/**
 * Created by LordBlackHole on 2017-01-07.
 */
public class Interpreter {

    private static final Logger log = LoggerFactory.getLogger(Interpreter.class);

    private final LibraryScope libraryScope;
    private final Path mainModule;
    private final Map<String, ModuleScope> modules = new HashMap<>();
    private final Caller caller;

    public Interpreter(String modulePath) {
        this.mainModule = Paths.get(modulePath).toAbsolutePath();
        this.libraryScope = new LibraryScope();
        this.caller = new Caller(this);
        new CoreLibrary(this).buildLibrary(libraryScope);

        addLibModule(new MapLibrary());
        addLibModule(new ListLibrary(this));
        addLibModule(new StringLibrary());
    }

    private void addLibModule(ModuleDefinition definition) {
        String name = definition.getName();
        ModuleScope moduleScope = new ModuleScope(libraryScope);

        definition.buildLibrary(moduleScope);

        modules.put(name, moduleScope);
        libraryScope.putValue(name, moduleScope);
    }

    private String resolveModule(String relativeModule) {
        return mainModule.resolveSibling(relativeModule).toAbsolutePath().toString();
    }

    public void run() {
        loadModuleReal(mainModule.toString());
    }

    public ModuleScope loadModule(String fileName) {
        String resolved = resolveModule(fileName);

        return modules.computeIfAbsent(resolved, this::loadModuleReal);
    }

    private ModuleScope loadModuleReal(String filePath) {
        try {
            CharStream fileStream = CharStreams.fromFileName(filePath, Charset.forName("UTF-8"));

            LetLexer lexer = new LetLexer(fileStream);

            CommonTokenStream tokens = new CommonTokenStream(lexer);

            LetParser parser = new LetParser(tokens);

            LetParser.ModuleContext context = parser.module();

            AstBuilder builder = new AstBuilder();

            Module module = builder.build(context);

            return (ModuleScope) eval(new LocalScope(libraryScope), module);
        } catch (IOException e) {
            throw new RuntimeException("Failed to import module: " + filePath, e);
        }
    }

    private Object eval(LocalScope scope, Assignment expression) {
        Object value = eval(scope, expression.getExp());
        scope.putValue(expression.getVar().getName(), value);
        log.info("let {} = {}", expression.getVar().getName(), value);
        return value;
    }

    private Object eval(LocalScope scope, Call call) {
        Object obj = eval(scope, call.getMethod());

        if (obj == null) {
            throw new RuntimeException("Undefined method " + call.getMethod().getLocation().print());
        }

        if(obj instanceof Func) {
            return caller.call(scope, (Func) obj, call.getArguments());
        } else {
            throw new RuntimeException("Unknown function type: " + obj.getClass());
        }
    }

    private Object eval(LocalScope scope, Export expression) {
        Object value = eval(scope, expression.getExpression());

        scope.exportValue(expression.getName(), value);

        return value;
    }

    private Object eval(LocalScope scope, Import expression) {
        ModuleScope importScope = loadModule(expression.getId());

        scope.putValue(expression.getModuleName(), importScope);

        return importScope;
    }

    private DefinedFunc eval(LocalScope scope, Lambda expression) {
        return (args) -> {

            LocalScope inner = new LocalScope(scope);
            List<Variable> variables = expression.getVariables();

            for (int i = 0; i < variables.size() && i < args.size(); i++) {
                inner.putValue(variables.get(i).getName(), args.get(i));
            }

            List<Object> collect = expression.getStatements().map(ex -> eval(inner, ex));

            return collect.get(collect.size() - 1);
        };
    }

    private Object eval(Literal expression) {
        return expression.getValue();
    }

    private Object eval(LocalScope scope, Module expression) {
        ModuleScope moduleScope = new ModuleScope(scope.getLibraryScope());
        LocalScope newLocal = new LocalScope(moduleScope);

        expression.getExpressions().forEach(ex -> eval(newLocal, ex));

        return moduleScope;
    }

    private Object eval(LocalScope scope, Parenthesized expression) {
        LocalScope inner = expression.isNeedsScope() ? new LocalScope(scope) : scope;

        return expression.getExpressions().foldLeft(null, (prev, ex) -> eval(inner, ex));
    }

    private Object eval(LocalScope scope, Variable expression) {
        return scope.getValue(expression.getName());
    }

    public Object eval(LocalScope scope, Expression expression) {
        return Match(expression).of(
                Case(instanceOf(Assignment.class), ex -> eval(scope, ex)),
                Case(instanceOf(Call.class), ex -> eval(scope, ex)),
                Case(instanceOf(Export.class), ex -> eval(scope, ex)),
                Case(instanceOf(Import.class), ex -> eval(scope, ex)),
                Case(instanceOf(Lambda.class), ex -> eval(scope, ex)),
                Case(instanceOf(Literal.class), this::eval),
                Case(instanceOf(Module.class), ex -> eval(scope, ex)),
                Case(instanceOf(Parenthesized.class), ex -> eval(scope, ex)),
                Case(instanceOf(Variable.class), ex -> eval(scope, ex))
        );
    }
}
