package com.redgear.let.ast;

import com.redgear.let.eval.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by LordBlackHole on 2016-12-30.
 */
public class Call implements Expression {

    private static final Logger log = LoggerFactory.getLogger(Call.class);
    private final Location location;
    private final Expression method;
    private final List<Expression> arguments;

    public Call(Location location, Expression method, List<Expression> arguments) {
        this.location = location;
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
    public Object eval(LocalScope scope) {

        Object obj = method.eval(scope);

        if (obj == null) {
            throw new RuntimeException("Undefined method " + method.getLocation().print());
        }

        if(obj instanceof Func) {
            return Caller.call(scope, (Func) obj, arguments);
        } else {
            throw new RuntimeException("Unknown function type: " + obj.getClass());
        }
    }

    @Override
    public Location getLocation() {
        return location;
    }
}
