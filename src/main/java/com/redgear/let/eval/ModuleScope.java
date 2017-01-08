package com.redgear.let.eval;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LordBlackHole on 2017-01-01.
 */
public class ModuleScope implements Scope {

    private final LibraryScope scope;
    private Map<String, Object> values = new HashMap<>();

    public ModuleScope(LibraryScope scope) {
        this.scope = scope;
    }

    @Override
    public Object getValue(String id) {
        if(values.containsKey(id)) {
            return values.get(id);
        } else {
            return scope.getValue(id);
        }
    }

    @Override
    public void putValue(String id, Object value) {
        values.put(id, value);
    }

    @Override
    public LibraryScope getLibraryScope() {
        return scope;
    }

    @Override
    public void exportValue(String id, Object value) {
        values.put(id, value);
    }

}
