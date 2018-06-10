package com.redgear.let.types;

import java.util.HashMap;
import java.util.Map;

public class ModuleTypeScope implements TypeScope {

    private final LibraryTypeScope parent;
    private final Map<String, TypeToken> declaredExports = new HashMap<>();

    public ModuleTypeScope(LibraryTypeScope parent) {
        this.parent = parent;
    }

    public TypeToken getType(String variable) {
        if (declaredExports.containsKey(variable)) {
            return declaredExports.get(variable);
        } else {
            return parent.getType(variable);
        }
    }

    public void exportType(String variable, TypeToken typeToken) {
        if (declaredExports.containsKey(variable)) {
            throw new RuntimeException("Attempt to reexport variable: " + variable);
        } else {
            declaredExports.put(variable, typeToken);
        }
    }
}
