package com.redgear.let.ast;

import com.redgear.let.types.TypeToken;
import javaslang.collection.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Call implements Expression {

    private static final Logger log = LoggerFactory.getLogger(Call.class);
    private final Location location;
    private final TypeToken typeToken;
    private final Expression method;
    private final List<Expression> arguments;

    public Call(Location location, TypeToken typeToken, Expression method, List<Expression> arguments) {
        this.location = location;
        this.typeToken = typeToken;
        this.method = method;
        this.arguments = arguments;
    }

    public Expression getMethod() {
        return method;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    public String getName() {
        if (method instanceof Variable) {
            return ((Variable) method).getName();
        } else {
            return "anon";
        }
    }

    @Override
    public String toString() {
        return "{\"className\": \"" + Call.class + "\"" +
                ",\"method\": \"" + method + '\'' + "\"" +
                ",\"arguments\": \"" + arguments + "\"" +
                '}';
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
