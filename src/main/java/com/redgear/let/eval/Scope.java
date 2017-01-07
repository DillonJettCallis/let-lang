package com.redgear.let.eval;

/**
 * Created by LordBlackHole on 2017-01-01.
 */
public interface Scope {
    Object getValue(String module, String id);

    Object getValue(String id);

    void putValue(String id, Object value);

    default void putFunc(String name, Eval.LibFunc func) {
        putValue(name, func);
    }

    default void putMacroFunc(String name, Eval.MacroFunc func) {
        putValue(name, func);
    }
}
