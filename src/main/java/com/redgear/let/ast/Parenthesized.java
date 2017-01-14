package com.redgear.let.ast;

import com.redgear.let.eval.LocalScope;
import javaslang.collection.List;

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
        this.needsScope = expressions.find(ex -> ex instanceof Assignment).isDefined();
    }


    public List<Expression> getExpressions() {
        return expressions;
    }

    public boolean isNeedsScope() {
        return needsScope;
    }

    @Override
    public Object eval(LocalScope scope) {
        LocalScope inner = needsScope ? new LocalScope(scope) : scope;

        List<Object> collect = expressions.map(ex -> ex.eval(inner));

        return collect.get(collect.size() - 1);
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
