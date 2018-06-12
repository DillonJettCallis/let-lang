package com.redgear.let.types;

import javaslang.collection.List;

import java.util.Objects;

public class SimpleFunctionTypeToken implements FunctionTypeToken {

    private final List<TypeToken> argTypes;
    private final TypeToken resultType;

    public SimpleFunctionTypeToken(List<TypeToken> argTypes, TypeToken resultType) {
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
    public TypeToken getResultType(List<TypeToken> argTypes) {
        if (this.argTypes.equals(argTypes)) {
            return resultType;
        } else {
            return null;
        }
    }

    public FunctionTypeToken setResultType(TypeToken resultType) {
        return new SimpleFunctionTypeToken(argTypes, resultType);
    }

    @Override
    public String getName() {
        return "{" + argTypes.map(TypeToken::getName).mkString(", ") + " => " + resultType.getName() + " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleFunctionTypeToken that = (SimpleFunctionTypeToken) o;
        return Objects.equals(argTypes, that.argTypes) &&
                Objects.equals(resultType, that.resultType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(argTypes, resultType);
    }
}
