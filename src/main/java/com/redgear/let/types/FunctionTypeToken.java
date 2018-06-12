package com.redgear.let.types;

import javaslang.collection.List;

public interface FunctionTypeToken extends TypeToken {

    TypeToken getResultType(List<TypeToken> argTypes);

}
