package com.redgear.let.ast;

import com.redgear.let.types.OverloadedFunctionTypeToken;
import com.redgear.let.types.TypeToken;
import javaslang.collection.List;

public class OverloadedFunction implements Expression {

    private final Location location;
    private final OverloadedFunctionTypeToken typeToken;
    private final List<Lambda> overloads;


    public OverloadedFunction(Location location, OverloadedFunctionTypeToken typeToken, List<Lambda> overloads) {
        this.location = location;
        this.typeToken = typeToken;
        this.overloads = overloads;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public OverloadedFunctionTypeToken getTypeToken() {
        return typeToken;
    }

    public Lambda findMatchingOverload(List<TypeToken> argTypes) {
        var index = typeToken.getSignatureMatch(argTypes);

        if (index == -1) {
            return null;
        } else {
            return overloads.get(index);
        }
    }

    public List<Lambda> getOverloads() {
        return overloads;
    }
}
