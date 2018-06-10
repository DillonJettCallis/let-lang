package com.redgear.let.types;

import java.util.HashMap;
import java.util.Map;

public class LocalTypeScope implements TypeScope {

    private final TypeScope parent;
    private final Map<String, TypeToken> declaredVars = new HashMap<>();

    public LocalTypeScope(TypeScope parent) {
        this.parent = parent;
    }

    public TypeToken getType(String variable) {
        if (declaredVars.containsKey(variable)) {
            return declaredVars.get(variable);
        } else {
            return parent.getType(variable);
        }
    }

    public void declareType(String variable, TypeToken typeToken) {
        if (declaredVars.containsKey(variable)) {
            throw new RuntimeException("Attempt to redeclare variable: " + variable);
        } else {
            declaredVars.put(variable, typeToken);
        }
    }

    public void exportType(String variable, TypeToken typeToken) {
        parent.exportType(variable, typeToken);
    }

}
