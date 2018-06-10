package com.redgear.let.ast;

import com.redgear.let.types.TypeToken;
import javaslang.collection.List;

public class Parenthesized implements Expression {

    private final Location location;
    private final TypeToken typeToken;
    private final List<Expression> expressions;
    private final boolean needsScope;

    public Parenthesized(Location location, TypeToken typeToken, List<Expression> expressions) {
        this.location = location;
        this.typeToken = typeToken;
        this.expressions = expressions;
        this.needsScope = expressions.find(ex -> ex instanceof Assignment).isDefined();
    }

    public TypeToken getTypeToken() {
        return typeToken;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    public boolean isNeedsScope() {
        return needsScope;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "{\"className\": \"" + Parenthesized.class + "\"" +
                ",\"location\": \"" + location + "\"" +
                ",\"expressions\": \"" + expressions + "\"" +
                '}';
    }
}
