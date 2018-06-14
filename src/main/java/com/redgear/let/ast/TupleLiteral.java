package com.redgear.let.ast;

import com.redgear.let.types.TypeToken;
import javaslang.collection.List;

public class TupleLiteral implements Expression {

    private final Location location;
    private final List<Expression> contents;
    private final TypeToken typeToken;

    public TupleLiteral(Location location, TypeToken typeToken, List<Expression> contents) {
        this.location = location;
        this.contents = contents;
        this.typeToken = typeToken;
    }

    public List<Expression> getValues() {
        return contents;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public TypeToken getTypeToken() {
        return typeToken;
    }
}
