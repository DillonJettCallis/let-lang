package com.redgear.brace.ast;

import com.redgear.brace.lex.Location;

/**
 * Created by LordBlackHole on 2017-01-02.
 */
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
