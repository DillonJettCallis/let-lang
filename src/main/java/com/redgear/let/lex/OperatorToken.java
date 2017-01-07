package com.redgear.let.lex;

/**
 * Created by LordBlackHole on 2017-01-01.
 */
public class OperatorToken implements Token {

    private final Location location;
    private final String value;

    public OperatorToken(Location location, String value) {
        this.location = location;
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Location getLocation() {
        return location;
    }
}
