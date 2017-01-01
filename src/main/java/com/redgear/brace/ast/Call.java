package com.redgear.brace.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LordBlackHole on 2016-12-30.
 */
public class Call implements Expression {

    private ModuleRef moduleRef;
    private String method;
    private List<Expression> arguments;

    public Call(ModuleRef moduleRef, String method) {
        this(moduleRef, method, new ArrayList<>());
    }

    public Call(ModuleRef moduleRef, String method, Expression expression) {
        this(moduleRef, method, singleton(expression));
    }

    public Call(ModuleRef moduleRef, String method, List<Expression> arguments) {
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

    public void setModuleRef(ModuleRef moduleRef) {
        this.moduleRef = moduleRef;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    public void setArguments(List<Expression> arguments) {
        this.arguments = arguments;
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
