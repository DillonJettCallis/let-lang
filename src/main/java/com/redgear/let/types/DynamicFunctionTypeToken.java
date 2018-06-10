package com.redgear.let.types;

import javaslang.collection.List;

import java.util.function.Function;

public class DynamicFunctionTypeToken implements TypeToken {

    private final String name;
    private final Function<List<TypeToken>, TypeToken> resultTypeCalculator;

    public DynamicFunctionTypeToken(String name, Function<List<TypeToken>, TypeToken> resultTypeCalculator) {
        this.name = name;
        this.resultTypeCalculator = resultTypeCalculator;
    }


    public TypeToken getResultType(List<TypeToken> args) {
        return resultTypeCalculator.apply(args);
    }


    @Override
    public String getName() {
        return name;
    }
}
