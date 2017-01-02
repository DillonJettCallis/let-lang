package com.redgear.brace.ast;

import com.redgear.brace.lex.Location;

/**
 * Created by LordBlackHole on 2016-12-30.
 */
public class Assignment implements Expression {


    private final Location location;
    private final Variable var;
    private final Expression exp;

    public Assignment(Location location, Variable var, Expression exp) {
        this.location = location;
        this.var = var;
        this.exp = exp;

    }

    public Variable getVar() {
        return var;
    }

    public Expression getExp() {
        return exp;
    }



    @Override
    public String toString() {
        return "{\"className\": \"" + Assignment.class + "\"" +
                ",\"var\": \"" + var + "\"" +
                ",\"exp\": \"" + exp + "\"" +
                '}';
    }

    @Override
    public Location getLocation() {
        return location;
    }
}
