package com.redgear.let.eval;

import java.util.HashMap;
import java.util.Map;

public class LibraryScope implements Scope {

    private final Map<String, Object> values = new HashMap<>();

    @Override
    public Object getValue(String id) {
        return values.get(id);
    }

    @Override
    public void putValue(String id, Object value) {
        values.put(id, value);
    }

    @Override
    public LibraryScope getLibraryScope() {
        return this;
    }

    @Override
    public ModuleScope importModule(String module) {
        return null;
    }

    @Override
    public void declareImport(String alias, ModuleScope moduleScope) {

    }


    @Override
    public void exportValue(String id, Object value) {

    }
}
