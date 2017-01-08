package com.redgear.let.ast;

import com.redgear.let.eval.LocalScope;

/**
 * Created by LordBlackHole on 2016-12-30.
 */
public class Variable implements Expression {

    private final Location location;
    private final String name;

    public Variable(Location location, String name) {
        this.location = location;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "{\"className\": \"" + Variable.class + "\"" +
                ",\"location\": \"" + location + "\"" +
                ",\"name\": \"" + name + '\'' + "\"" +
                '}';
    }

    @Override
    public Object eval(LocalScope scope) {
        return scope.getValue(name);
    }

    @Override
    public Location getLocation() {
        return location;
    }

}
