package com.redgear.let.types;

import com.redgear.let.lib.*;

import java.util.HashMap;
import java.util.Map;

public class LibraryTypeScope implements TypeScope {

    private final Map<String, ModuleTypeScope> coreModules = new HashMap<>();
    private final Map<String, TypeToken> declaredBuiltins = new HashMap<>();

    public LibraryTypeScope() {
        new CoreLibrary().buildTypes(this);
        loadLibModule(new ListLibrary());
        loadLibModule(new MapLibrary());
        loadLibModule(new StringLibrary());
    }

    private void loadLibModule(ModuleDefinition definition) {
        var moduleScope = new ModuleTypeScope(this);
        definition.buildTypes(moduleScope);
        this.coreModules.put(definition.getName(), moduleScope);
    }

    @Override
    public TypeToken getType(String variable) {
        return declaredBuiltins.get(variable);
    }

    @Override
    public void exportType(String variable, TypeToken typeToken) {

    }

    @Override
    public ModuleTypeScope importModule(String name) {
        return coreModules.get(name);
    }

    @Override
    public void declareImport(String name, ModuleTypeScope module) {
        coreModules.put(name, module);
    }

    public void declareType(String name, TypeToken typeToken) {
        declaredBuiltins.put(name, typeToken);
    }

    public Map<String, ModuleTypeScope> getCoreModules() {
        return coreModules;
    }
}
