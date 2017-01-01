package com.redgear.brace.ast;

/**
 * Created by LordBlackHole on 2017-01-01.
 */
public class ThisModule extends ModuleRef {

    private static final ThisModule instance = new ThisModule();

    public ThisModule() {
        super("this");
    }

    public static ThisModule get() {
        return instance;
    }

}
