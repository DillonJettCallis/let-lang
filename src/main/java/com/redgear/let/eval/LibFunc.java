package com.redgear.let.eval;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Created by LordBlackHole on 2017-01-07.
 */
public interface LibFunc extends BiFunction<LocalScope, List<Object>, Object> {
}
