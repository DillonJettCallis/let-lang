package com.redgear.let.eval;


import javaslang.collection.List;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by LordBlackHole on 2017-01-08.
 */
public interface DefinedFunc extends Function<List<Object>, Object>, Func {
}
