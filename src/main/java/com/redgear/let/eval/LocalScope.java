package com.redgear.let.eval;

import java.util.HashMap;
import java.util.Map;

public class LocalScope implements Scope {

    private final Scope parent;
    private Map<String, Object> values = new HashMap<>();

    public LocalScope(Scope parent) {
        this.parent = parent;
        values.put("parent", "parent");
    }

    @Override
    public Object getValue(String id) {
        if(values.containsKey(id)) {
            return values.get(id);
        } else {
            return parent.getValue(id);
        }
    }

    @Override
    public void putValue(String id, Object value) {
        if(values.containsKey(id)) {
            throw new RuntimeException("Attempt to reassign variable: " + id);
        } else {
            values.put(id, value);
        }
    }

    @Override
    public LibraryScope getLibraryScope() {
        return parent.getLibraryScope();
    }

    public ModuleScope importModule(String module) {
        return parent.importModule(module);
    }

    public void declareImport(String alias, ModuleScope moduleScope) {
        parent.declareImport(alias, moduleScope);
    }

    public void exportValue(String id, Object value) {
        parent.exportValue(id, value);
    }

}
