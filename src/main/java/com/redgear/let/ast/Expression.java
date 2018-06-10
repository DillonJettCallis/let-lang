package com.redgear.let.ast;

import com.redgear.let.types.TypeToken;

public interface Expression {

    Location getLocation();

    TypeToken getTypeToken();

}
