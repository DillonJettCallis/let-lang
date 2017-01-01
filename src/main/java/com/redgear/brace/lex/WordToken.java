package com.redgear.brace.lex;

/**
 * Created by LordBlackHole on 2017-01-01.
 */
public class WordToken implements Token {

    private final Location location;
    private final String name;


    public WordToken(Location location, String name) {
        this.location = location;
        this.name = name;
    }

    public String getValue() {
        return name;
    }

    @Override
    public Location getLocation() {
        return location;
    }
}
