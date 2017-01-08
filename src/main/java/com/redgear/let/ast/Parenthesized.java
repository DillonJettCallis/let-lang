package com.redgear.let.ast;

import java.util.List;

/**
 * Created by LordBlackHole on 2017-01-07.
 */
public class Parenthesized implements Expression {

    private final Location location;
    private final List<Expression> expressions;
    private final boolean needsScope;

    public Parenthesized(Location location, List<Expression> expressions) {
        this.location = location;
        this.expressions = expressions;
        this.needsScope = expressions.stream().anyMatch(ex -> ex instanceof Assignment);
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
