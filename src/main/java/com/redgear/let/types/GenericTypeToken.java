package com.redgear.let.types;

import javaslang.collection.List;

import java.util.Objects;

public class GenericTypeToken implements TypeToken {

    private final TypeToken typeConstructor;
    private final List<TypeToken> typeParams;

    public GenericTypeToken(TypeToken typeConstructor, List<TypeToken> typeParams) {
        this.typeConstructor = typeConstructor;
        this.typeParams = typeParams;
    }

    public TypeToken getTypeConstructor() {
        return typeConstructor;
    }

    public List<TypeToken> getTypeParams() {
        return typeParams;
    }

    @Override
    public String getName() {
        return typeConstructor.getName() + "<" + typeParams.map(TypeToken::getName).mkString(", ") + ">";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenericTypeToken that = (GenericTypeToken) o;
        return Objects.equals(typeConstructor, that.typeConstructor) &&
                Objects.equals(typeParams, that.typeParams);
    }

    @Override
    public int hashCode() {

        return Objects.hash(typeConstructor, typeParams);
    }
}
