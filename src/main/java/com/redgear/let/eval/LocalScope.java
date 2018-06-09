package com.redgear.let.eval;

import java.util.HashMap;
import java.util.Map;

public class LocalScope implements Scope {

    private final Scope local;
    private Map<String, Object> values = new HashMap<>();

    public LocalScope(Scope local) {
        this.local = local;
        values.put("local", "local");
    }

    @Override
    public Object getValue(String id) {
        if(values.containsKey(id)) {
            return values.get(id);
        } else {
            return local.getValue(id);
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
        return local.getLibraryScope();
    }

    public void exportValue(String id, Object value) {
        local.exportValue(id, value);
    }

}
