package com.redgear.let.types;

import javaslang.collection.List;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.Predicates.instanceOf;

public class GenericFunctionTypeToken implements TypeToken {

    private final List<ParamaterTypeToken> typeParameters;
    private final List<TypeToken> argTypes;
    private final TypeToken resultType;

    public GenericFunctionTypeToken(List<ParamaterTypeToken> typeParameters, List<TypeToken> argTypes, TypeToken resultType) {
        this.typeParameters = typeParameters;
        this.argTypes = argTypes;
        this.resultType = resultType;

        verifyConstruction();
    }

    public TypeToken getResultType(List<TypeToken> args) {
        return null;
    }

    private void verifyConstruction() {
        Set<String> paramNames = new HashSet<>();

        typeParameters.forEach(param -> {
            var name = param.getName();

            if (paramNames.contains(name)) {
                throw new RuntimeException("Cannot reuse same type parameter name twice: " + name);
            } else {
                paramNames.add(name);
            }
        });

        if (resultType instanceof GenericTypeToken) {
            var name = resultType.getName();

            if (!paramNames.contains(name)) {
                throw new RuntimeException("Unknown named type parameter: " + name);
            }
        }

        Set<String> usedParams = new HashSet<>();

        Consumer<TypeToken> consumer = token -> {
            if (token instanceof GenericTypeToken) {
                var name = token.getName();

                if (!paramNames.contains(name)) {
                    throw new RuntimeException("Unknown named type parameter: " + name);
                } else {
                    usedParams.add(name);
                }
            }
        };

        argTypes.forEach(type -> traverseConstruction(type, consumer));

        if (!paramNames.equals(usedParams)) {
            throw new RuntimeException("Unused parameter");
        }
    }

    private Void traverseConstruction(TypeToken token, Consumer<TypeToken> consumer) {
        consumer.accept(token);
        Match(token).of(
                Case(instanceOf(DynamicFunctionTypeToken.class), arg -> {
                    throw new RuntimeException("Illegal use of DynamicFunctionTypeToken: This should never be used in a function call");
                }),
                Case(instanceOf(FunctionTypeToken.class), arg -> {
                    arg.getArgTypes().forEach(type -> traverseConstruction(type, consumer));

                    return traverseConstruction(arg.getResultType(), consumer);
                }),
                Case(instanceOf(GenericFunctionTypeToken.class), arg -> {
                    throw new RuntimeException("Illegal use of GenericFunctionTypeToken: This should never be used in a function call");
                }), Case(instanceOf(GenericTypeToken.class), arg -> {
                    arg.getTypeParams().forEach(type -> traverseConstruction(type, consumer));
                    return null;
                })
        );

        return null;
    }

    @Override
    public String getName() {
        var params = typeParameters.map(ParamaterTypeToken::getName).mkString(", ");
        var args = argTypes.map(TypeToken::getName).mkString(", ");
        var result = resultType.getName();

        return "{<" + params + "> " + args + " => " + result + "}";
    }
}
