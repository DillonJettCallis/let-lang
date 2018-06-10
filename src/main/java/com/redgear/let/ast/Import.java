package com.redgear.let.ast;

import com.redgear.let.types.TypeToken;

public class Import implements Expression {

    private final Location location;
    private final TypeToken typeToken;
    private final String path;
    private final String alias;


    public Import(Location location, TypeToken typeToken, String path, String alias) {
        this.location = location;
        this.typeToken = typeToken;
        this.path = path;
        this.alias = alias;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public TypeToken getTypeToken() {
        return typeToken;
    }

    public String getPath() {
        return path;
    }

    public String getAlias() {
        return alias;
    }
}
