package com.redgear.let.ast;

import com.redgear.let.eval.DefinedFunc;
import com.redgear.let.eval.LibFunc;
import com.redgear.let.eval.LocalScope;
import com.redgear.let.eval.MacroFunc;
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

        LocalScope inner = new LocalScope(scope);


        if (obj instanceof DefinedFunc) {
            DefinedFunc func = (DefinedFunc) obj;

            List<Object> args = arguments.stream().map(ex -> ex.eval(scope)).collect(Collectors.toList());

            return func.apply(args);
        } else if (obj instanceof LibFunc) {
            try {
                LibFunc func = (LibFunc) obj;

                List<Object> args = arguments.stream().map(ex -> ex.eval(scope)).collect(Collectors.toList());

                return func.apply(inner, args);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage() + " " + method.getLocation().print(), e);
            }
        } else if (obj instanceof MacroFunc) {
            MacroFunc func = (MacroFunc) obj;


            return func.apply(inner, arguments);
        } else {
            throw new RuntimeException("Unknown function type: " + obj.getClass());
        }

    }

    @Override
    public Location getLocation() {
        return location;
    }
}
