package com.redgear.let.eval;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by LordBlackHole on 2017-01-01.
 */
public class LibraryScope implements Scope {

    private Map<String, Object> values = new HashMap<>();
    private Map<String, ModuleScope> modules = new HashMap<>();

    @Override
    public Object getValue(String module, String id) {
        if(modules.containsKey(module)) {
            return modules.get(module).getValue(id);
        } else {
            return null;
        }
    }

    public ModuleScope getModule(String name) {
        return modules.get(name);
    }

    public void putModule(ModuleScope mod) {
        modules.put(mod.getName(), mod);
    }

    public Set<String> modules() {
        return modules.keySet();
    }

    @Override
    public Object getValue(String id) {
        return values.get(id);
    }

    @Override
    public void putValue(String id, Object value) {
        values.put(id, value);
    }

    public boolean hasModule(String id) {
        return modules.containsKey(id);
    }
}
