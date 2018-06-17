package com.redgear.let.types;

import javaslang.collection.List;

import java.util.Objects;

public class OverloadedFunctionTypeToken implements FunctionTypeToken {

    private final List<FunctionTypeToken> implementations;

    public OverloadedFunctionTypeToken(List<FunctionTypeToken> implementations) {
        this.implementations = implementations;
    }

    public List<FunctionTypeToken> getImplementations() {
        return implementations;
    }

    @Override
    public SimpleFunctionTypeToken getResolvedType(List<TypeToken> argTypes) {
        return implementations.map(impl -> impl.getResolvedType(argTypes)).find(Objects::nonNull).getOrElse((SimpleFunctionTypeToken) null);
    }

    public int getSignatureMatch(List<TypeToken> argTypes) {
        return implementations.map(impl -> impl.getResolvedType(argTypes)).indexWhere(Objects::nonNull);
    }

    @Override
    public String getName() {
        return implementations.map(FunctionTypeToken::getName).mkString("(", ") or (", ")");
    }
}
