package com.redgear.let.ast;

public class Import implements Expression {

    private final Location location;
    private final String path;
    private final String alias;


    public Import(Location location, String path, String alias) {
        this.location = location;
        this.path = path;
        this.alias = alias;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public String getPath() {
        return path;
    }

    public String getAlias() {
        return alias;
    }
}
