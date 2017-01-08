package com.redgear.let.util;

import com.redgear.let.ast.AstBuilder;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Created by LordBlackHole on 2017-01-08.
 */
public class OptionalWrapper<T> {

    private final Optional<T> optional;


    public OptionalWrapper(Optional<T> optional) {
        this.optional = optional;
    }


    public OptionalWrapper<T> orElse(Supplier<Optional<T>> source) {
        if (optional.isPresent())
            return this;
        else
            return new OptionalWrapper<>(source.get());
    }

    public Optional<T> get() {
        return optional;
    }

}
