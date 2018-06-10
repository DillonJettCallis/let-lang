package com.redgear.let.ast;

import com.redgear.let.types.TypeToken;

public class Variable implements Expression {

    private final Location location;
    private final TypeToken typeToken;
    private final String name;

    public Variable(Location location, TypeToken typeToken, String name) {
        this.location = location;
        this.typeToken = typeToken;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public TypeToken getTypeToken() {
        return typeToken;
    }

    public Variable setTypeToken(TypeToken typeToken) {
        return new Variable(location, typeToken, name);
    }

    @Override
    public String toString() {
        return "{\"className\": \"" + Variable.class + "\"" +
                ",\"location\": \"" + location + "\"" +
                ",\"name\": \"" + name + '\'' + "\"" +
                '}';
    }

    @Override
    public Location getLocation() {
        return location;
    }

}
