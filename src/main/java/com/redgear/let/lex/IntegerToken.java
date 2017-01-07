package com.redgear.let.lex;

/**
 * Created by LordBlackHole on 2017-01-01.
 */
public class IntegerToken implements LiteralToken {

    private final Location location;
    private final int value;

    public IntegerToken(Location location, int value) {
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