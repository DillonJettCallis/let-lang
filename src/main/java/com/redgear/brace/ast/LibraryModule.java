package com.redgear.brace.ast;

/**
 * Created by LordBlackHole on 2017-01-01.
 */
public class LibraryModule extends ModuleRef {

    private static final LibraryModule instance = new LibraryModule();

    public LibraryModule() {
        super("lib");
    }

    public static LibraryModule get() {
        return instance;
    }
}
