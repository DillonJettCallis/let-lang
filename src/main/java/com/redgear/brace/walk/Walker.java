package com.redgear.brace.walk;

import com.redgear.brace.ast.*;

import java.util.Objects;

/**
 * Created by LordBlackHole on 2016-12-30.
 */
public interface Walker {



    default void walk(Expression expression) {

        Objects.requireNonNull(expression);

        if(expression instanceof Assignment) {
            walk((Assignment) expression);
        } else if(expression instanceof Literal) {
            walk((Literal) expression);
        } else if(expression instanceof Module) {
            walk((Module) expression);
        } else if(expression instanceof Variable) {
            walk((Variable) expression);
        } else if(expression instanceof Call) {
            walk((Call) expression);
        } else if(expression instanceof Func) {
            walk((Func) expression);
        } else {
            throw new RuntimeException("Unknown type: " + expression);
        }

    }

    void walk(Assignment assignment);

    void walk(Literal literal);

    void walk(Module module);

    void walk(Variable variable);

    void walk(Call call);

    void walk(Func func);

}
