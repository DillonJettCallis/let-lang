package com.redgear.let.ast;

import com.redgear.let.eval.LocalScope;
import com.redgear.let.eval.ModuleScope;
import javaslang.collection.List;

/**
 * Created by LordBlackHole on 2016-12-30.
 */
public class Module implements Expression {

    private final Location location;
    private final List<Expression> expressions;


    public Module(Location location, List<Expression> expressions) {
        this.location = location;
        this.expressions = expressions;
    }


    public List<Expression> getExpressions() {
        return expressions;
    }

    @Override
    public String toString() {
        return "{\"className\": \"" + Module.class + "\"" +
                ",\"location\": \"" + location + "\"" +
                ",\"expressions\": \"" + expressions + "\"" +
                '}';
    }

    @Override
    public Object eval(LocalScope scope) {
        ModuleScope moduleScope = new ModuleScope(scope.getLibraryScope());
        LocalScope newLocal = new LocalScope(moduleScope);

        expressions.forEach(ex -> ex.eval(newLocal));

        return moduleScope;
    }

    @Override
    public Location getLocation() {
        return location;
    }
}
