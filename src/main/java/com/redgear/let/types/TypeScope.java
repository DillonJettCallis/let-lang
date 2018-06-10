package com.redgear.let.types;

public interface TypeScope {

    TypeToken getType(String variable);

    void exportType(String variable, TypeToken typeToken);

    ModuleTypeScope importModule(String name);

    void declareImport(String name, ModuleTypeScope module);

    void declareType(String name, TypeToken typeToken);

}
