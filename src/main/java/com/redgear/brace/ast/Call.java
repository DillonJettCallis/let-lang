package com.redgear.brace.ast;

import com.redgear.brace.lex.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LordBlackHole on 2016-12-30.
 */
public class Call implements Expression {

    private final Location location;
    private final ModuleRef moduleRef;
    private final Expression method;
    private final List<Expression> arguments;

    public Call(Location location, ModuleRef moduleRef, Expression method) {
        this(location, moduleRef, method, new ArrayList<>());
    }

    public Call(Location location, ModuleRef moduleRef, Expression method, Expression expression) {
        this(location, moduleRef, method, singleton(expression));
    }

    public Call(Location location, ModuleRef moduleRef, Expression method, List<Expression> arguments) {
        this.location = location;
        this.moduleRef = moduleRef;
        this.method = method;
        this.arguments = arguments;
    }

    private static List<Expression> singleton(Expression expression) {
        List<Expression> list = new ArrayList<>();
        list.add(expression);
        return list;
    }

    public ModuleRef getModuleRef() {
        return moduleRef;
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
                ",\"moduleRef\": \"" + moduleRef + "\"" +
                ",\"method\": \"" + method + '\'' + "\"" +
                ",\"arguments\": \"" + arguments + "\"" +
                '}';
    }

    @Override
    public Location getLocation() {
        return location;
    }
}
