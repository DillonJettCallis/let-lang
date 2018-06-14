package com.redgear.let.ast;

import com.redgear.let.types.TypeToken;
import javaslang.collection.List;

public class MapLiteral implements Expression {

    private final Location location;
    private final TypeToken typeToken;
    private final List<Expression> keys;
    private final List<Expression> values;

    public MapLiteral(Location location, TypeToken typeToken, List<Expression> keys, List<Expression> values) {
        this.location = location;
        this.typeToken = typeToken;
        this.keys = keys;
        this.values = values;
    }

    public List<Expression> getKeys() {
        return keys;
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
