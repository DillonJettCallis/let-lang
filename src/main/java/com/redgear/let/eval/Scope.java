package com.redgear.let.eval;

public interface Scope {

    Object getValue(String id);

    void putValue(String id, Object value);

    LibraryScope getLibraryScope();

    void exportValue(String id, Object value);

    default void putFunc(String name, LibFunc func) {
        putValue(name, func);
    }

    default void putMacroFunc(String name, MacroFunc func) {
        putValue(name, func);
    }
}
