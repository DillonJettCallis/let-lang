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
        var keyType = new ParamaterTypeToken("Key");
        var valueType = new ParamaterTypeToken("Value");
        var inputMap = LiteralTypeToken.mapTypeToken.construct(List.of(keyType, valueType));

        var baseParams = List.of(keyType, valueType);

        typeScope.declareType("get", new GenericFunctionTypeToken(baseParams, List.of(inputMap, keyType), valueType));
    }
}
