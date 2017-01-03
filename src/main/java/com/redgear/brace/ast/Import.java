package com.redgear.brace.ast;

import com.redgear.brace.lex.Location;

/**
 * Created by LordBlackHole on 2017-01-02.
 */
public class Import implements Expression {

    private final Location location;
    private final ModuleRef module;
    private final String id;

    public Import(Location location, ModuleRef module, String id) {
        this.location = location;
        this.module = module;
        this.id = id;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public ModuleRef getModule() {
        return module;
    }

    public String getId() {
        return id;
    }
}
