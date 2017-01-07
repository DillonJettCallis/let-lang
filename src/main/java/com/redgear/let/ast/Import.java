package com.redgear.let.ast;

import com.redgear.let.lex.Location;

/**
 * Created by LordBlackHole on 2017-01-02.
 */
public class Import implements Expression {

    private final Location location;
    private final String moduleName;
    private final String id;

    public Import(Location location, String moduleName, String id) {
        this.location = location;
        this.moduleName = moduleName;
        this.id = id;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getId() {
        return id;
    }
}
