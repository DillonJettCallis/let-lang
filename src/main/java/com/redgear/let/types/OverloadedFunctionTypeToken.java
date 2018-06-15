package com.redgear.let.types;

import javaslang.collection.List;

import java.util.Objects;

public class OverloadedFunctionTypeToken implements FunctionTypeToken {

    private final List<SimpleFunctionTypeToken> implementations;

    public OverloadedFunctionTypeToken(List<SimpleFunctionTypeToken> implementations) {
        this.implementations = implementations;
    }

    public List<SimpleFunctionTypeToken> getImplementations() {
        return implementations;
    }

    @Override
    public SimpleFunctionTypeToken getResolvedType(List<TypeToken> argTypes) {
        return implementations.map(impl -> impl.getResolvedType(argTypes)).find(Objects::nonNull).getOrElse((SimpleFunctionTypeToken) null);
    }

    @Override
    public String getName() {
        return implementations.map(SimpleFunctionTypeToken::getName).mkString("(", ") or (", ")");
    }
}
