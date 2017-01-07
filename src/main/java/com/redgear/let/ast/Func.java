package com.redgear.let.ast;

import com.redgear.let.lex.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LordBlackHole on 2017-01-01.
 */
public class Func implements Expression {

    private final Location location;
    private final List<Variable> args;
    private final List<Expression> statements;

    public Func(Location location) {
        this(location, new ArrayList<>(), new ArrayList<>());
    }

    public Func(Location location, List<Variable> args, List<Expression> statements) {
        this.location = location;
        this.args = args;
        this.statements = statements;
    }

    public List<Variable> getArgs() {
        return args;
    }

    public List<Expression> getStatements() {
        return statements;
    }

    @Override
    public Location getLocation() {
        return location;
    }
}
