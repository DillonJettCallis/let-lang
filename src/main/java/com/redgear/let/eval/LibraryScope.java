package com.redgear.let.eval;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LordBlackHole on 2017-01-01.
 */
public class LibraryScope implements Scope {

    private Map<String, Object> values = new HashMap<>();

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
    public void exportValue(String id, Object value) {

    }
}
