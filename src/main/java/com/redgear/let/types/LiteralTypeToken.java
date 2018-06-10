package com.redgear.let.types;

public class LiteralTypeToken implements TypeToken {

    private final String name;

    public static final LiteralTypeToken stringTypeToken = new LiteralTypeToken("String");
    public static final LiteralTypeToken intTypeToken = new LiteralTypeToken("Int");
    public static final LiteralTypeToken floatTypeToken = new LiteralTypeToken("Float");
    public static final LiteralTypeToken nullTypeToken = new LiteralTypeToken("null");

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
        } else {
            throw new RuntimeException("Unknown literal type: " + value.getClass());
        }
    }

}
