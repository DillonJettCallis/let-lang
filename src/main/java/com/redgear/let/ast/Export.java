package com.redgear.let.ast;

import com.redgear.let.types.TypeToken;

public class Export implements Expression {

    private final Location location;
    private final TypeToken typeToken;
    private final String name;
    private final Expression expression;

    public Export(Location location, TypeToken typeToken, String name, Expression expression) {
        this.location = location;
        this.typeToken = typeToken;
        this.name = name;
        this.expression = expression;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public TypeToken getTypeToken() {
        return typeToken;
    }

    public Export setBody(Expression body) {
        return new Export(location, body.getTypeToken(), name, body);
    }

    public String getName() {
        return name;
    }

    public Expression getExpression() {
        return expression;
    }
}
