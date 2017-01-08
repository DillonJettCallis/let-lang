package com.redgear.let.ast;

import com.redgear.let.eval.Interpreter;
import com.redgear.let.eval.LocalScope;
import com.redgear.let.eval.ModuleScope;

/**
 * Created by LordBlackHole on 2017-01-02.
 */
public class Import implements Expression {

    private final Location location;
    private final Interpreter interpreter;
    private final String moduleName;
    private final String id;

    public Import(Location location, Interpreter interpreter, String moduleName, String id) {
        this.location = location;
        this.interpreter = interpreter;
        this.moduleName = moduleName;
        this.id = id;
    }

    @Override
    public Object eval(LocalScope scope) {

        ModuleScope importScope = interpreter.loadModule(id);

        scope.putValue(moduleName, importScope);

        return importScope;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getId() {
        return id;
    }
}
