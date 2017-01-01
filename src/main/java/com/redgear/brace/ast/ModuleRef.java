package com.redgear.brace.ast;

/**
 * Created by LordBlackHole on 2017-01-01.
 */
public class ModuleRef {

    private String name;

    public ModuleRef(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
