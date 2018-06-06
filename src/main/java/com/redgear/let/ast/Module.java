package com.redgear.let.ast;

import javaslang.collection.List;

public class Module implements Expression {

    private final Location location;
    private final List<Expression> expressions;


    public Module(Location location, List<Expression> expressions) {
        this.location = location;
        this.expressions = expressions;
    }


    public List<Expression> getExpressions() {
        return expressions;
    }

    @Override
    public String toString() {
        return "{\"className\": \"" + Module.class + "\"" +
                ",\"location\": \"" + location + "\"" +
                ",\"expressions\": \"" + expressions + "\"" +
                '}';
    }

    @Override
    public Location getLocation() {
        return location;
    }
}
