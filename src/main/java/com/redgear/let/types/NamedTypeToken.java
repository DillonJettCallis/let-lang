package com.redgear.let.types;

public class NamedTypeToken implements TypeToken {

    private final String name;

    public NamedTypeToken(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
