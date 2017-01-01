package com.redgear.brace.lex;

/**
 * Created by LordBlackHole on 2017-01-01.
 */
public class DoubleToken implements LiteralToken {

    private final Location location;
    private final double value;

    public DoubleToken(Location location, double value) {
        this.location = location;
        this.value = value;
    }

    @Override
    public String getValue() {
        return String.valueOf(value);
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public Object getRealValue() {
        return value;
    }
}
