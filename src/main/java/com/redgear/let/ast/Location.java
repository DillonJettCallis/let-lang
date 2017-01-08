package com.redgear.let.ast;

import org.antlr.v4.runtime.Token;

/**
 * Created by LordBlackHole on 2017-01-07.
 */
public class Location {

    private final Token token;

    public Location(Token token) {
        this.token = token;
    }

    public String print() {
        return "At row: " + String.valueOf(token.getLine()) + ", column: " + String.valueOf(token.getCharPositionInLine());
    }

}
