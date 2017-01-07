package com.redgear.let.js;

import com.redgear.let.ast.*;
import com.redgear.let.walk.PrintingWalker;

import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by LordBlackHole on 2017-01-02.
 */
public class JavascriptTranspiler extends PrintingWalker {

    private static final Set<String> ops = new HashSet<>();
    private static final Map<String, Consumer<Call>> builtIns = new HashMap<>();

    public JavascriptTranspiler(Writer writer) {
        super(writer);
        ops.add("+");
        ops.add("-");
        ops.add("*");
        ops.add("/");
        ops.add("**");
        ops.add("&&");
        ops.add("||");

        builtIns.put("if", call -> {

            walk(call.getArguments().get(0));
            print(" ? ");
            walk(call.getArguments().get(1));
            print(" : ");
            walk(call.getArguments().get(2));

        });

        builtIns.put("==", call -> {
            print("(");
            walk(call.getArguments().get(0));
            print(" === ");
            walk(call.getArguments().get(1));
            print(")");
        });

        builtIns.put("!=", call -> {
            print("(");
            walk(call.getArguments().get(0));
            print(" !== ");
            walk(call.getArguments().get(1));
            print(")");
        });
    }

    @Override
    public void walk(Assignment assignment) {
        print(printIndent(), "const ", assignment.getVar().getName(), " = ");

        walk(assignment.getExp());

        print(";\n");
    }

    @Override
    public void walk(Literal literal) {
        Object value = literal.getValue();

        if(value == Boolean.FALSE) {
            print("false");
        } else if(value == Boolean.TRUE) {
            print("true");
        } else if(value == null) {
            print("null");
        } else if(value instanceof String) {
            print("'", (String) value, "'");
        } else if(value instanceof Number) {
            print(String.valueOf(value));
        } else {
            throw new RuntimeException("Unknown literal type: " + value.getClass());
        }

    }

    public void walk(Module module) {
        module.getExpressions().forEach(this::walk);
    }

    @Override
    public void walk(Variable variable) {
        print(variable.getName());
    }

    @Override
    public void walk(Call call) {
        if(call.getMethod() instanceof Variable) {
            String name = ((Variable) call.getMethod()).getName();

            if(ops.contains(name)) {

                if(call.getArguments().size() == 1) {
                    print("(");
                    print(" ", name, " ");
                    walk(call.getArguments().get(0));
                    print(")");
                    return;
                }
                if(call.getArguments().size() == 2) {
                    print("(");
                    walk(call.getArguments().get(0));
                    print(" ", name, " ");
                    walk(call.getArguments().get(1));
                    print(")");
                    return;
                }
            } else if(builtIns.containsKey(name)) {
                builtIns.get(name).accept(call);
                return;
            }

        }

        walk(call.getMethod());

        print("(");

        if(call.getArguments().size() == 0) {
            print(")");
        } else {
            walk(call.getArguments().get(0));
            call.getArguments().stream().skip(1).forEach(arg -> {
                print(", ");
                walk(arg);
            });
            print(")");
        }
    }

    @Override
    public void walk(Func func) {
        print(printIndent(), "function ", func.getArgs().stream().map(Variable::getName).collect(Collectors.joining(",", "(", ")")), "{\n");

        indent++;
        int size = func.getStatements().size();

        func.getStatements().stream().limit(size - 1).forEach(this::walk);

        Expression last = func.getStatements().get(size - 1);

        if(last instanceof Assignment) {
            walk(last);
            print(printIndent(), "return ", ((Assignment) last).getVar().getName(), ";\n");
        } else {
            print(printIndent(), "return ");
            walk(last);
            print(";\n");
        }

        indent--;
        print(printIndent(), "}");
    }

    @Override
    public void walk(Import im) {

    }

    @Override
    public void walk(Export ex) {

    }
}
