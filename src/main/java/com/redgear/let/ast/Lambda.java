package com.redgear.let.ast;

import com.redgear.let.types.TypeToken;
import javaslang.collection.List;

public class Lambda implements Expression {

    private final Location location;
    private final TypeToken typeToken;
    private final List<Variable> variables;
    private final List<Expression> statements;

    public Lambda(Location location, TypeToken typeToken, List<Variable> variables, List<Expression> statements) {
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
    public TypeToken getTypeToken() {
        return typeToken;
    }

    public Lambda setTypeToken(TypeToken typeToken) {
        return new Lambda(location, typeToken, variables, statements);
    }

    public Lambda setBody(List<Expression> statements) {
        var bodyTypeToken = statements.last().getTypeToken();

        if (bodyTypeToken != null) {
            if (typeToken != null && !typeToken.equals(bodyTypeToken)) {
                throw new RuntimeException("Function declared type differs from returned type. Declared: " + typeToken);
            }

            return new Lambda(location, bodyTypeToken, variables, statements);
        } else {
            return new Lambda(location, typeToken, variables, statements);
        }
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
