package com.redgear.brace.ast;

/**
 * Created by LordBlackHole on 2016-12-30.
 */
public class Assignment implements Expression {

    private Variable var;

    private Expression exp;

    public Assignment(Variable var, Expression exp) {
        this.var = var;
        this.exp = exp;
    }

    public Variable getVar() {
        return var;
    }

    public void setVar(Variable var) {
        this.var = var;
    }

    public Expression getExp() {
        return exp;
    }

    public void setExp(Expression exp) {
        this.exp = exp;
    }

    @Override
    public String toString() {
        return "{\"className\": \"" + Assignment.class + "\"" +
                ",\"var\": \"" + var + "\"" +
                ",\"exp\": \"" + exp + "\"" +
                '}';
    }
}
