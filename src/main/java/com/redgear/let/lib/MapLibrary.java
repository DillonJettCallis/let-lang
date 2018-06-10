package com.redgear.let.lib;


import com.redgear.let.eval.Interpreter;
import com.redgear.let.eval.Scope;
import com.redgear.let.types.*;
import javaslang.Tuple;
import javaslang.collection.List;
import javaslang.collection.Map;

public class MapLibrary implements ModuleDefinition {

    @Override
    public String getName() {
        return "Core.Map";
    }

    @Override
    public void buildLibrary(Interpreter interpreter, Scope moduleScope) {

        moduleScope.putFunc("build", (scope, args) -> args.sliding(2, 2).toMap(pair -> Tuple.of(pair.get(0), pair.get(1))));

        moduleScope.putFunc("get", (scope, args) -> {

            if(args.size() != 2) {
                throw new RuntimeException("Wrong number of arguments for function 'Map.get', found: " + args);
            }

            Object first = args.get(0);
            Object second = args.get(1);

            if(first instanceof Map) {
                return ((Map) first).get(second).getOrElse((Object) null);
            } else {
                throw new RuntimeException("Illegal argument for function Map.get: Expected Map, found: " + (first == null ? "null" : first.getClass()));
            }
        });
    }

    @Override
    public void buildTypes(TypeScope typeScope) {
        typeScope.declareType("build", new DynamicFunctionTypeToken("build", MapLibrary::buildMap));

        typeScope.declareType("get", new DynamicFunctionTypeToken("get", args -> {
            if (args.size() == 2) {
                var rawMap = args.head();
                var key = args.get(1);

                var keyType = extractKey(rawMap);
                var valueType = extractValue(rawMap);

                if (key.equals(keyType)) {
                    return valueType;
                }
            }

            return null;
        }));
    }

    public TypeToken extractKey(TypeToken token) {
        return extractType(token, 0);
    }

    public TypeToken extractValue(TypeToken token) {
        return extractType(token, 1);
    }

    public TypeToken extractType(TypeToken token, int index) {
        if (token instanceof GenericTypeToken) {
            var gen = (GenericTypeToken) token;

            if (gen.getTypeConstructor().equals(LiteralTypeToken.mapTypeToken.getBase())) {
                var params = gen.getTypeParams();

                if (params.size() == 2) {
                    return params.get(index);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    static TypeToken buildMap(List<TypeToken> args) {
        if (args.size() % 2 == 0) {
            var keyType = args.head();
            var valueType = args.last();

            boolean isValid = args.sliding(2, 2).foldLeft(true, (prev, next) -> {
                if (prev) {
                    var key = next.head();
                    var value = next.last();

                    return keyType.equals(key) && valueType.equals(value);
                } else {
                    return false;
                }
            });

            if (isValid) {
                return LiteralTypeToken.mapTypeToken.construct(List.of(keyType, valueType));
            }
        }

        return null;
    }
}
