package com.redgear.let.eval;

import com.redgear.let.ast.Expression;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Created by LordBlackHole on 2017-01-07.
 */
public interface MacroFunc extends BiFunction<LocalScope, List<Expression>, Object> {
}
