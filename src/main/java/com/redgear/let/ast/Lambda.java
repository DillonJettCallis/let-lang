package com.redgear.let.ast;

import javaslang.collection.List;

/**
 * Created by LordBlackHole on 2017-01-01.
 */
public class Lambda implements Expression {

    private final Location location;
    private final List<Variable> variables;
    private final List<Expression> statements;

    public Lambda(Location location, List<Variable> variables, List<Expression> statements) {
        this.location = location;
        this.variables = variables;
        this.statements = statements;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public List<Expression> getStatements() {
        return statements;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "{\"className\": \"" + Lambda.class + "\"" +
                ",\"location\": \"" + location + "\"" +
                ",\"variables\": \"" + variables + "\"" +
                ",\"statements\": \"" + statements + "\"" +
                '}';
    }
}
