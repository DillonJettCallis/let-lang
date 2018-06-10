package com.redgear.let.ast;

import com.redgear.let.types.LiteralTypeToken;
import com.redgear.let.types.TypeToken;

public class Literal implements Expression {

    private final Location location;
    private final TypeToken typeToken;
    private final Object value;

    public Literal(Location location, Object value) {
        this.location = location;
        this.value = value;
        this.typeToken = LiteralTypeToken.getTypeToken(value);
    }

    public TypeToken getTypeToken() {
        return typeToken;
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
