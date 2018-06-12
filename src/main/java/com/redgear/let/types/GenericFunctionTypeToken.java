package com.redgear.let.types;

import javaslang.collection.List;

import java.util.*;
import java.util.function.Consumer;

import static javaslang.API.$;
import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.Predicates.instanceOf;

public class GenericFunctionTypeToken implements FunctionTypeToken {

    private final List<ParamaterTypeToken> typeParameters;
    private final List<TypeToken> argTypes;
    private final TypeToken resultType;

    public GenericFunctionTypeToken(List<ParamaterTypeToken> typeParameters, List<TypeToken> argTypes, TypeToken resultType) {
        this.typeParameters = typeParameters;
        this.argTypes = argTypes;
        this.resultType = resultType;

//        verifyConstruction();
    }

    public List<ParamaterTypeToken> getTypeParameters() {
        return typeParameters;
    }

    public List<TypeToken> getArgTypes() {
        return argTypes;
    }

    public TypeToken getResultType() {
        return resultType;
    }

    public TypeToken getResultType(List<TypeToken> args) {
        var params = new HashMap<String, TypeToken>();

        if (args.size() == getArgTypes().size()) {
            try {
                args.zip(getArgTypes()).forEach(pair -> {
                    traverseMatch(pair._1, pair._2, params);
                });
            } catch (RuntimeException e) {
                return null;
            }
        } else {
            return null;
        }

        return fillParams(resultType, params);
    }

    private TypeToken fillParams(TypeToken typeToken, Map<String, TypeToken> params) {
        if (typeToken instanceof ParamaterTypeToken) {
            var name = typeToken.getName();
            return params.get(name);
        } else if (typeToken instanceof GenericTypeToken) {
            var genType = (GenericTypeToken) typeToken;

            return new GenericTypeToken(genType.getTypeConstructor(), genType.getTypeParams().map(inner -> fillParams(inner, params)));
        } else {
            return typeToken;
        }
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

        if (getResultType() instanceof ParamaterTypeToken) {
            var name = getResultType().getName();

            if (!paramNames.contains(name)) {
                throw new RuntimeException("Unknown named type parameter: " + name);
            }
        }

        Set<String> usedParams = new HashSet<>();

        Consumer<TypeToken> consumer = token -> {
            if (token instanceof ParamaterTypeToken) {
                var name = token.getName();

                if (!paramNames.contains(name)) {
                    throw new RuntimeException("Unknown named type parameter: " + name);
                } else {
                    usedParams.add(name);
                }
            }
        };

        getArgTypes().forEach(type -> traverseConstruction(type, consumer));

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
                Case(instanceOf(SimpleFunctionTypeToken.class), arg -> {
                    arg.getArgTypes().forEach(type -> traverseConstruction(type, consumer));

                    return traverseConstruction(arg.getResultType(), consumer);
                }),
                Case(instanceOf(GenericFunctionTypeToken.class), arg -> {
                    throw new RuntimeException("Illegal use of GenericFunctionTypeToken: This should never be used in a function call");
                }), Case(instanceOf(GenericTypeToken.class), arg -> {
                    arg.getTypeParams().forEach(type -> traverseConstruction(type, consumer));
                    return null;
                }), Case($(), () -> null)
        );

        return null;
    }

    private void traverseMatch(TypeToken given, TypeToken template, Map<String, TypeToken> params) {

        if (template instanceof ParamaterTypeToken) {
            var genTemplate = (ParamaterTypeToken) template;
            var varName = genTemplate.getName();

            params.merge(varName, given, (prev, next) -> {
                if (prev.equals(next)) {
                    return prev;
                } else {
                    throw new RuntimeException("Non-matching type parameters");
                }
            });
        } else if (template instanceof GenericFunctionTypeToken && given instanceof SimpleFunctionTypeToken) {
            var functionTemplate = (GenericFunctionTypeToken) template;
            var functionGiven = (SimpleFunctionTypeToken) given;

            traverseMatch(functionGiven.getResultType(), functionTemplate.getResultType(), params);

            if (functionGiven.getArgTypes().size() == functionTemplate.getArgTypes().size()) {
                functionGiven.getArgTypes().zip(functionTemplate.getArgTypes()).forEach(pair -> {
                    traverseMatch(pair._1, pair._2, params);
                });
            } else {
                throw new RuntimeException("Non-matching type parameters");
            }
        } else if (given instanceof SimpleFunctionTypeToken && template instanceof SimpleFunctionTypeToken) {
            var functionGiven = (SimpleFunctionTypeToken) given;
            var functionTemplate = (SimpleFunctionTypeToken) template;

            traverseMatch(functionGiven.getResultType(), functionTemplate.getResultType(), params);

            if (functionGiven.getArgTypes().size() == functionTemplate.getArgTypes().size()) {
                functionGiven.getArgTypes().zip(functionTemplate.getArgTypes()).forEach(pair -> {
                    traverseMatch(pair._1, pair._2, params);
                });
            } else {
                throw new RuntimeException("Non-matching type parameters");
            }
        } else if (given instanceof LiteralTypeToken && template instanceof LiteralTypeToken) {
            var functionGiven = (LiteralTypeToken) given;
            var functionTemplate = (LiteralTypeToken) template;

            if (!functionGiven.equals(functionTemplate)) {
                throw new RuntimeException("Non-matching type parameters");
            }
        } else if (given instanceof GenericTypeToken && template instanceof GenericTypeToken) {
            var functionGiven = (GenericTypeToken) given;
            var functionTemplate = (GenericTypeToken) template;

            traverseMatch(functionGiven.getTypeConstructor(), functionTemplate.getTypeConstructor(), params);

            if (functionGiven.getTypeParams().size() == functionTemplate.getTypeParams().size()) {
                functionGiven.getTypeParams().zip(functionTemplate.getTypeParams()).forEach(pair -> {
                    traverseMatch(pair._1, pair._2, params);
                });
            } else {
                throw new RuntimeException("Non-matching type parameters");
            }
        } else {
            throw new RuntimeException("Non-matching type parameters");
        }
    }


    @Override
    public String getName() {
        var params = typeParameters.map(ParamaterTypeToken::getName).mkString(", ");
        var args = getArgTypes().map(TypeToken::getName).mkString(", ");
        var result = getResultType().getName();

        return "{<" + params + "> " + args + " => " + result + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenericFunctionTypeToken that = (GenericFunctionTypeToken) o;
        return Objects.equals(typeParameters, that.typeParameters) &&
                Objects.equals(argTypes, that.argTypes) &&
                Objects.equals(resultType, that.resultType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeParameters, argTypes, resultType);
    }
}
