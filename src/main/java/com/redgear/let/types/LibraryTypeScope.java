package com.redgear.let.types;

import java.util.HashMap;
import java.util.Map;

public class LibraryTypeScope implements TypeScope {

    private final Map<String, TypeToken> declaredTypes = new HashMap<>();

    public LibraryTypeScope() {

    }

    @Override
    public TypeToken getType(String variable) {
        return declaredTypes.get(variable);
    }

    @Override
    public void exportType(String variable, TypeToken typeToken) {

    }

    @Override
    public ModuleTypeScope importModule(String name) {
        return null;
    }

    @Override
    public void declareImport(String name, ModuleTypeScope module) {

    }

    public void declareType(String name, TypeToken typeToken) {
        declaredTypes.put(name, typeToken);
    }
}
