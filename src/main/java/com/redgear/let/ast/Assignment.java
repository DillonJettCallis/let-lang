package com.redgear.let.ast;

import com.redgear.let.types.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Assignment implements Expression {

    private static final Logger log = LoggerFactory.getLogger(Assignment.class);
    private final Location location;
    private final TypeToken typeToken;
    private final Variable var;
    private final Expression body;

    public Assignment(Location location, TypeToken typeToken, Variable var, Expression body) {
        this.location = location;
        this.typeToken = typeToken;
        this.var = var;
        this.body = body;

    }

    public Variable getVar() {
        return var;
    }

    public Expression getBody() {
        return body;
    }

    public Assignment setBody(Expression body) {
        return new Assignment(location, body.getTypeToken(), var, body);
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public TypeToken getTypeToken() {
        return typeToken;
    }
}
