package com.redgear.brace.ast;

import com.redgear.brace.lex.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LordBlackHole on 2016-12-30.
 */
public class Module implements Expression {

    private final Location location;
    private final String name;
    private final List<Expression> expressions;

    public Module(Location location, String name) {
        this(location, name, new ArrayList<>());
    }

    public Module(Location location, String name, List<Expression> expressions) {
        this.location = location;
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

    @Override
    public Location getLocation() {
        return location;
    }
}
