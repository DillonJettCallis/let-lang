package com.redgear.brace.ast;

/**
 * Created by LordBlackHole on 2016-12-30.
 */
public class Variable implements Expression {

    private String name;


    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "{\"className\": \"" + Variable.class + "\"" +
                ",\"name\": \"" + name + '\'' + "\"" +
                '}';
    }

}
