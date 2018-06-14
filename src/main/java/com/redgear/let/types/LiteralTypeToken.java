package com.redgear.let.types;

import java.util.Objects;

public class LiteralTypeToken implements TypeToken {

    private final String name;

    public static final LiteralTypeToken stringTypeToken = new LiteralTypeToken("String");
    public static final LiteralTypeToken intTypeToken = new LiteralTypeToken("Int");
    public static final LiteralTypeToken floatTypeToken = new LiteralTypeToken("Float");
    public static final LiteralTypeToken booleanTypeToken = new LiteralTypeToken("Boolean");
    public static final LiteralTypeToken nullTypeToken = new LiteralTypeToken("null");
    public static final LiteralTypeToken unitTypeToken = new LiteralTypeToken("Unit");
    public static final TypeConstructorTypeToken listTypeToken = new TypeConstructorTypeToken(new LiteralTypeToken("List"), 1);
    public static final TypeConstructorTypeToken mapTypeToken = new TypeConstructorTypeToken(new LiteralTypeToken("Map"), 2);
    public static final LiteralTypeToken tupleTypeToken = new LiteralTypeToken("Tuple");

    public LiteralTypeToken(String name) {
        this.name = name;
    }

    public static LiteralTypeToken getTypeToken(Object value) {
        if (value == null) {
            return nullTypeToken;
        } else if (value instanceof String) {
            return stringTypeToken;
        } else if (value instanceof Integer) {
            return intTypeToken;
        } else if (value instanceof Double) {
            return floatTypeToken;
        } else if (value instanceof Boolean) {
            return booleanTypeToken;
        } else {
            throw new RuntimeException("Unknown literal type: " + value.getClass());
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiteralTypeToken that = (LiteralTypeToken) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
