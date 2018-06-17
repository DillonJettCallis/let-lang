package com.redgear.let.types;

public interface SingleFunctionTypeToken extends FunctionTypeToken {

    TypeToken getResultType();

    SingleFunctionTypeToken setResultType(TypeToken resultType);

}
