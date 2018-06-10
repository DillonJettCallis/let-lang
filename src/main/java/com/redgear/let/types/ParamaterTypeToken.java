package com.redgear.let.types;

public class ParamaterTypeToken implements TypeToken {

    private final String name;

    public ParamaterTypeToken(String name) {
        this.name = name;
    }


    @Override
    public String getName() {
        return name;
    }
}
