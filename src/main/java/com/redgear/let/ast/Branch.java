package com.redgear.let.ast;

import com.redgear.let.types.TypeToken;

public class Branch implements Expression {

    private final Location location;
    private final TypeToken resultType;
    private final Expression condition;
    private final Expression thenBlock;
    private final Expression elseBlock;


    public Branch(Location location, TypeToken resultType, Expression condition, Expression thenBlock, Expression elseBlock) {
        this.location = location;
        this.resultType = resultType;
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public TypeToken getTypeToken() {
        return resultType;
    }

    public Expression getCondition() {
        return condition;
    }

    public Expression getThenBlock() {
        return thenBlock;
    }

    public Expression getElseBlock() {
        return elseBlock;
    }
}
