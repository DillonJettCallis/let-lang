package com.redgear.let.types;

public interface TypeScope {

    TypeToken getType(String variable);

    void exportType(String variable, TypeToken typeToken);

}
