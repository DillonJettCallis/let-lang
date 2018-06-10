package com.redgear.let.types;

import java.util.HashMap;
import java.util.Map;

public class ModuleTypeScope implements TypeScope {

    private final LibraryTypeScope parent;
    private final Map<String, ModuleTypeScope> declaredImports = new HashMap<>();
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

    public ModuleTypeScope importModule(String name) {
        if (declaredImports.containsKey(name)) {
            return declaredImports.get(name);
        } else {
            return parent.importModule(name);
        }
    }

    public void declareImport(String name, ModuleTypeScope module) {
        declaredImports.put(name, module);
    }

    public void declareType(String name, TypeToken typeToken) {
        exportType(name, typeToken);
    }
}
