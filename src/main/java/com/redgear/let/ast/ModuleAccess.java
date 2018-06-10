package com.redgear.let.ast;

import com.redgear.let.types.TypeToken;

public class ModuleAccess implements Expression {

    private final Location location;
    private final TypeToken typeToken;
    private final String module;
    private final String access;

    public ModuleAccess(Location location, TypeToken typeToken, String module, String access) {
        this.location = location;
        this.typeToken = typeToken;
        this.module = module;
        this.access = access;
    }


    public String getModule() {
        return module;
    }

    public String getAccess() {
        return access;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public TypeToken getTypeToken() {
        return typeToken;
    }

    public ModuleAccess setTypeToken(TypeToken typeToken) {
        return new ModuleAccess(location, typeToken, module, access);
    }
}
