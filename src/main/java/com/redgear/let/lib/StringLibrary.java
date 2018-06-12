package com.redgear.let.lib;

import com.redgear.let.eval.Interpreter;
import com.redgear.let.eval.Scope;
import com.redgear.let.types.LiteralTypeToken;
import com.redgear.let.types.OverloadedFunctionTypeToken;
import com.redgear.let.types.SimpleFunctionTypeToken;
import com.redgear.let.types.TypeScope;
import javaslang.collection.List;

import java.util.Objects;

public class StringLibrary implements ModuleDefinition {

    @Override
    public String getName() {
        return "Core.String";
    }

    @Override
    public void buildLibrary(Interpreter interpreter, Scope moduleScope) {
        moduleScope.putFunc("split", (scope, args) -> {
            if (args.size() != 2) {
                throw new RuntimeException("Wrong number of arguments for function 'String.split', expected 2, found: " + args);
            }

            Object first = args.get(0);
            Object second = args.get(1);

            if (first instanceof String && second instanceof String) {
                return List.of(((String) first).split((String) second));
            } else {
                throw new RuntimeException("Illegal argument for function String.split: Expected (String, String), found: (" + (first == null ? "null" : first.getClass()) + ", " + (second == null ? "null" : second.getClass()) + ")");
            }
        });

        moduleScope.putFunc("size", (scope, args) -> {
            if (args.size() != 1) {
                throw new RuntimeException("Wrong number of arguments for function 'String.size', expected 1, found: " + args);
            }

            Object first = args.get(0);

            if (first instanceof String) {
                return ((String) first).length();
            } else {
                throw new RuntimeException("Illegal argument for function String.size: Expected (String), found: (" + (first == null ? "null" : first.getClass()) + ")");
            }
        });

        moduleScope.putFunc("toString", (scope, args) -> {
            if (args.size() != 1) {
                throw new RuntimeException("Wrong number of arguments for function 'String.size', expected 1, found: " + args);
            }

            return String.valueOf(args.get(0));
        });
    }

    @Override
    public void buildTypes(TypeScope typeScope) {
        typeScope.declareType("split", new SimpleFunctionTypeToken(List.of(LiteralTypeToken.stringTypeToken, LiteralTypeToken.stringTypeToken), LiteralTypeToken.listTypeToken.construct(List.of(LiteralTypeToken.stringTypeToken))));
        typeScope.declareType("size", new SimpleFunctionTypeToken(List.of(LiteralTypeToken.stringTypeToken), LiteralTypeToken.intTypeToken));


        var stringToString = new SimpleFunctionTypeToken(List.of(LiteralTypeToken.stringTypeToken), LiteralTypeToken.stringTypeToken);
        var intToString = new SimpleFunctionTypeToken(List.of(LiteralTypeToken.intTypeToken), LiteralTypeToken.stringTypeToken);
        var floatToString = new SimpleFunctionTypeToken(List.of(LiteralTypeToken.floatTypeToken), LiteralTypeToken.stringTypeToken);

        typeScope.declareType("toString", new OverloadedFunctionTypeToken(List.of(stringToString, intToString, floatToString)));
    }
}
