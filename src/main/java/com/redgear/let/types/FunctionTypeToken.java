package com.redgear.let.types;

import javaslang.collection.List;

import java.util.Objects;

public class FunctionTypeToken implements TypeToken {

    private final List<TypeToken> argTypes;
    private final TypeToken resultType;

    public FunctionTypeToken(List<TypeToken> argTypes, TypeToken resultType) {
        this.argTypes = argTypes;
        this.resultType = resultType;
    }

    public List<TypeToken> getArgTypes() {
        return argTypes;
    }

    public TypeToken getResultType() {
        return resultType;
    }

    @Override
    public String getName() {
        return "{" + argTypes.map(TypeToken::getName).mkString(", ") + " => " + resultType.getName() + " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionTypeToken that = (FunctionTypeToken) o;
        return Objects.equals(argTypes, that.argTypes) &&
                Objects.equals(resultType, that.resultType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(argTypes, resultType);
    }
}
