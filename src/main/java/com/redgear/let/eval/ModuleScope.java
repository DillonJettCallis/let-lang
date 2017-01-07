package com.redgear.let.eval;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LordBlackHole on 2017-01-01.
 */
public class ModuleScope implements Scope {

    private final String name;
    private final LibraryScope scope;
    private Map<String, Object> values = new HashMap<>();

    public ModuleScope(String name, LibraryScope scope) {
        this.name = name;
        this.scope = scope;
    }

    @Override
    public Object getValue(String module, String id) {
        if("this".equals(module) || name.equals(module)) {
            return getValue(id);
        } else {
            return scope.getValue(module, id);
        }
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

    public String getName() {
        return name;
    }
}
