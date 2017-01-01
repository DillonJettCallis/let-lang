package com.redgear.brace.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LordBlackHole on 2016-12-30.
 */
public class Module implements Expression {

    private final String name;
    private final List<Expression> expressions;

    public Module(String name) {
        this.name = name;
        this.expressions = new ArrayList<>();
    }

    public Module(String name, List<Expression> expressions) {
        this.name = name;
        this.expressions = expressions;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    @Override
    public String toString() {
        return "{\"className\": \"" + Module.class + "\"" +
                ",\"expressions\": \"" + expressions + "\"" +
                '}';
    }

    public String getName() {
        return name;
    }
}
