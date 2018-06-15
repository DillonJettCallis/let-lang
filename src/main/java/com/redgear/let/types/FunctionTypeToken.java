package com.redgear.let.types;

import javaslang.collection.List;

public interface FunctionTypeToken extends TypeToken {

    SimpleFunctionTypeToken getResolvedType(List<TypeToken> argTypes);

}
