package com.redgear.let.ast;

import com.redgear.let.lex.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LordBlackHole on 2016-12-30.
 */
public class Module implements Expression {

    private static final Location location = new Location(0, 0);
    private final List<Expression> expressions;

    public Module() {
        this(new ArrayList<>());
    }

    public Module(List<Expression> expressions) {
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

    @Override
    public Location getLocation() {
        return location;
    }
}
