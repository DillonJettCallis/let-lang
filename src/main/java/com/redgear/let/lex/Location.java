package com.redgear.let.lex;

/**
 * Created by LordBlackHole on 2017-01-01.
 */
public class Location {

    private final int row;
    private final int column;

    public Location(int row, int column) {
        this.row = row;
        this.column = column;
    }


    public String print() {
        return "At row: " + String.valueOf(row) + ", column: " + String.valueOf(column);
    }

}
