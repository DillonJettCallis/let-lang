package com.redgear.brace.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LordBlackHole on 2016-12-30.
 */
public class Call implements Expression {

    private final ModuleRef moduleRef;
    private final Expression method;
    private final List<Expression> arguments;

    public Call(ModuleRef moduleRef, Expression method) {
        this(moduleRef, method, new ArrayList<>());
    }

    public Call(ModuleRef moduleRef, Expression method, Expression expression) {
        this(moduleRef, method, singleton(expression));
    }

    public Call(ModuleRef moduleRef, Expression method, List<Expression> arguments) {
        this.moduleRef = moduleRef;
        this.method = method;
        this.arguments = arguments;
    }

    private static List<Expression> singleton(Expression expression) {
        List<Expression> list = new ArrayList<>();
        list.add(expression);
        return list;
    }

    public ModuleRef getModuleRef() {
        return moduleRef;
    }

    public Expression getMethod() {
        return method;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    public String getName() {
        if(method instanceof Variable) {
            return ((Variable) method).getName();
        } else {
            return "anon";
        }
    }

    @Override
    public String toString() {
        return "{\"className\": \"" + Call.class + "\"" +
                ",\"moduleRef\": \"" + moduleRef + "\"" +
                ",\"method\": \"" + method + '\'' + "\"" +
                ",\"arguments\": \"" + arguments + "\"" +
                '}';
    }
}
