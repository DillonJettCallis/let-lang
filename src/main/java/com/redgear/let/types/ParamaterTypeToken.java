package com.redgear.let.types;

import java.util.Objects;

public class ParamaterTypeToken implements TypeToken {

    private final String name;

    public ParamaterTypeToken(String name) {
        this.name = name;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParamaterTypeToken that = (ParamaterTypeToken) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }
}
