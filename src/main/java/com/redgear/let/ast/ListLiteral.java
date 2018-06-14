package com.redgear.let.ast;

import com.redgear.let.types.TypeToken;
import javaslang.collection.List;

public class ListLiteral implements Expression {

    private final Location location;
    private final TypeToken typeToken;
    private final List<Expression> values;


    public ListLiteral(Location location, TypeToken typeToken, List<Expression> values) {
        this.location = location;
        this.typeToken = typeToken;
        this.values = values;
    }

    public List<Expression> getValues() {
        return values;
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
