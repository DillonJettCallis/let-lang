package com.redgear.let.ast;

public class Export implements Expression {
    private final Location location;
    private final String name;
    private final Expression expression;

    public Export(Location location, String name, Expression expression) {
        this.location = location;
        this.name = name;
        this.expression = expression;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public Expression getExpression() {
        return expression;
    }
}
