package com.redgear.let.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LordBlackHole on 2016-12-30.
 */
public class Call implements Expression {

    private final Location location;
    private final Expression method;
    private final List<Expression> arguments;

    public Call(Location location, Expression method, List<Expression> arguments) {
        this.location = location;
        this.method = method;
        this.arguments = arguments;
    }

    private static List<Expression> singleton(Expression expression) {
        List<Expression> list = new ArrayList<>();
        list.add(expression);
        return list;
    }

    public Expression getMethod() {
        return method;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    public String getName() {
        if(method instanceof Variable) {
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
}
