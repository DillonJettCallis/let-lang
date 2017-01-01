package com.redgear.brace.ast;

/**
 * Created by LordBlackHole on 2016-12-30.
 */
public class Literal implements Expression {

    private Object value;

    public Literal(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "{\"className\": \"" + Literal.class + "\"" +
                ",\"value\": \"" + value + "\"" +
                '}';
    }

}
