package com.redgear.let.ast;

import com.redgear.let.eval.DefinedFunc;
import com.redgear.let.eval.LocalScope;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by LordBlackHole on 2017-01-01.
 */
public class Lambda implements Expression {

    private final Location location;
    private final List<Variable> variables;
    private final List<Expression> statements;

    public Lambda(Location location) {
        this(location, new ArrayList<>(), new ArrayList<>());
    }

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
    public DefinedFunc eval(LocalScope scope) {
        return (args) -> {

            LocalScope inner = new LocalScope(scope);

            for (int i = 0; i < variables.size() && i < args.size(); i++) {
                inner.putValue(variables.get(i).getName(), args.get(i));
            }

            List<Object> collect = statements.stream().map(ex -> ex.eval(inner)).collect(Collectors.toList());

            return collect.get(collect.size() - 1);
        };
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
