package com.redgear.let.ast;

/**
 * Created by LordBlackHole on 2016-12-30.
 */
public class Literal implements Expression {

    private final Location location;
    private final Object value;

    public Literal(Location location, Object value) {
        this.location = location;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "{\"className\": \"" + Literal.class + "\"" +
                ",\"value\": \"" + value + "\"" +
                '}';
    }

    @Override
    public Location getLocation() {
        return location;
    }

}
