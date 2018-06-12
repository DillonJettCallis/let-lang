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
    public TypeToken getResultType(List<TypeToken> argTypes) {
        return implementations.map(impl -> impl.getResultType(argTypes)).find(Objects::nonNull).getOrElse((TypeToken) null);
    }

    @Override
    public String getName() {
        return implementations.map(SimpleFunctionTypeToken::getName).mkString("(", ") or (", ")");
    }
}
