package com.redgear.let.types;

import javaslang.collection.List;
import javaslang.collection.Stream;

import java.util.Objects;

public class TypeConstructorTypeToken implements TypeToken {

    private final TypeToken base;
    private final int arity;

    public TypeConstructorTypeToken(TypeToken base, int arity) {
        this.base = base;
        this.arity = arity;
    }

    public GenericTypeToken construct(List<TypeToken> typeParams) {
        if (typeParams.size() == arity) {
            return new GenericTypeToken(base, typeParams);
        } else {
            throw new RuntimeException("Not enough type parameters for type: " + getName() + ", found: " + base.getName() + "<" + typeParams.map(TypeToken::getName).mkString(", ") + ">");
        }
    }

    public TypeToken getBase() {
        return base;
    }

    @Override
    public String getName() {
        return base.getName() + "<" + Stream.range(0, arity).map(i -> "?").mkString(", ") + ">";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeConstructorTypeToken that = (TypeConstructorTypeToken) o;
        return arity == that.arity &&
                Objects.equals(base, that.base);
    }

    @Override
    public int hashCode() {

        return Objects.hash(base, arity);
    }
}
