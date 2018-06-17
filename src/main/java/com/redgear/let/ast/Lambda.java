package com.redgear.let.ast;

import com.redgear.let.types.SingleFunctionTypeToken;
import javaslang.collection.List;

public class Lambda implements Expression {

    private final Location location;
    private final SingleFunctionTypeToken typeToken;
    private final List<Variable> variables;
    private final List<Expression> statements;

    public Lambda(Location location, SingleFunctionTypeToken typeToken, List<Variable> variables, List<Expression> statements) {
        this.location = location;
        this.typeToken = typeToken;
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
    public SingleFunctionTypeToken getTypeToken() {
        return typeToken;
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
