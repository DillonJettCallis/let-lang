package com.redgear.let.types;

import java.util.HashMap;
import java.util.Map;

public class LibraryTypeScope implements TypeScope {

    private final Map<String, TypeToken> declaredBuiltins = new HashMap<>();


    @Override
    public TypeToken getType(String variable) {
        return declaredBuiltins.get(variable);
    }

    @Override
    public void exportType(String variable, TypeToken typeToken) {

    }

    public void declareType(String name, TypeToken typeToken) {
        declaredBuiltins.put(name, typeToken);
    }
}
