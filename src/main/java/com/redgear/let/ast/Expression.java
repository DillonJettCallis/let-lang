package com.redgear.let.ast;

import com.redgear.let.eval.LocalScope;

/**
 * Created by LordBlackHole on 2016-12-30.
 */
public interface Expression {

    Object eval(LocalScope scope);

    Location getLocation();

}
