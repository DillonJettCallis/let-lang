package com.redgear.let.lib;

import com.redgear.let.eval.Interpreter;
import com.redgear.let.eval.Scope;
import com.redgear.let.types.*;
import javaslang.Tuple;
import javaslang.collection.List;

import java.util.Objects;

public class CoreLibrary implements ModuleDefinition {

    @Override
    public String getName() {
        return "Core.Core";
    }

    public void buildLibrary(Interpreter interpreter, Scope libraryScope) {
        libraryScope.putValue("_", null);
        libraryScope.putValue("true", true);
        libraryScope.putValue("false", false);

        libraryScope.putFunc("+", (scope, args) -> {

            validateArgs("+", args, 2);

            Object left = args.get(0);
            Object right = args.get(1);

            if (left instanceof Integer && right instanceof Integer) {
                return (Integer) left + (Integer) right;
            } else if(left instanceof Number && right instanceof Number) {
                return ((Number) left).doubleValue() + ((Number) right).doubleValue();
            } else {
                throw new RuntimeException("Illegal arguments to '+' op, found: " + args);
            }

        });

        libraryScope.putFunc("-", (scope, args) -> {

            validateArgs("-", args, 1, 2);

            if(args.size() == 1) {
                Object value = args.get(0);

                if (value instanceof Integer ) {
                    return -(Integer) value;
                } else if (value instanceof Number ) {
                    return -((Number) value).doubleValue();
                } else {
                    throw new RuntimeException("Illegal arguments to '-' op, found: " + args);
                }
            } else {

                Object left = args.get(0);
                Object right = args.get(1);

                if (left instanceof Integer && right instanceof Integer) {
                    return (Integer) left - (Integer) right;
                } else if (left instanceof Number && right instanceof Number) {
                    return ((Number) left).doubleValue() - ((Number) right).doubleValue();
                } else {
                    throw new RuntimeException("Illegal arguments to '-' op, found: " + args);
                }
            }
        });

        libraryScope.putFunc("*", (scope, args) -> {

            validateArgs("*", args, 2);

            Object left = args.get(0);
            Object right = args.get(1);

            if (left instanceof Integer && right instanceof Integer) {
                return (Integer) left * (Integer) right;
            } else if(left instanceof Number && right instanceof Number) {
                return ((Number) left).doubleValue() * ((Number) right).doubleValue();
            } else {
                throw new RuntimeException("Illegal arguments to '*' op, found: " + args);
            }

        });

        libraryScope.putFunc("/", (scope, args) -> {

            validateArgs("/", args, 2);

            Object left = args.get(0);
            Object right = args.get(1);

            if(left instanceof Number && right instanceof Number) {
                return ((Number) left).doubleValue() / ((Number) right).doubleValue();
            } else {
                throw new RuntimeException("Illegal arguments to '/' op, found: " + args);
            }

        });

        libraryScope.putFunc("**", (scope, args) -> {

            validateArgs("**", args, 2);

            Object left = args.get(0);
            Object right = args.get(1);

            if(left instanceof Number && right instanceof Number) {
                return Math.pow(((Number) left).doubleValue(), ((Number) right).doubleValue());
            } else {
                throw new RuntimeException("Illegal arguments to '**' op, found: " + args);
            }

        });

        libraryScope.putFunc("==", (scope, args) -> {

            validateArgs("==", args, 2);

            Object left = args.get(0);
            Object right = args.get(1);

            return Objects.equals(left, right);
        });

        libraryScope.putFunc("!=", (scope, args) -> {

            validateArgs("!=", args, 2);

            Object left = args.get(0);
            Object right = args.get(1);

            return !Objects.equals(left, right);
        });

        libraryScope.putFunc("<", (scope, args) -> {

            validateArgs("<", args, 2);

            Object left = args.get(0);
            Object right = args.get(1);

            if(left == null || right == null) {
                throw new RuntimeException("Cannot compare against null: ");
            }

            if(left instanceof Integer && right instanceof Integer) {
                return (Integer) left < (Integer) right;
            } else if(left instanceof Number && right instanceof Number) {
                return ((Number) left).doubleValue() < ((Number) right).doubleValue();
            } else if(left instanceof String && right instanceof String) {
                return ((String) left).compareTo((String) right) < 0;
            } else {
                throw new RuntimeException("Cannot compare two different values: " + left.getClass() + " < " + right.getClass());
            }
        });

        libraryScope.putFunc("=<", (scope, args) -> {

            validateArgs("=<", args, 2);

            Object left = args.get(0);
            Object right = args.get(1);

            if(left == null || right == null) {
                throw new RuntimeException("Cannot compare against null: ");
            }

            if(left instanceof Integer && right instanceof Integer) {
                return (Integer) left <= (Integer) right;
            } else if(left instanceof Number && right instanceof Number) {
                return ((Number) left).doubleValue() <= ((Number) right).doubleValue();
            } else if(left instanceof String && right instanceof String) {
                return ((String) left).compareTo((String) right) <= 0;
            } else {
                throw new RuntimeException("Cannot compare two different values: " + left.getClass() + " <= " + right.getClass());
            }
        });

        libraryScope.putFunc(">", (scope, args) -> {

            validateArgs(">", args, 2);

            Object left = args.get(0);
            Object right = args.get(1);

            if(left == null || right == null) {
                throw new RuntimeException("Cannot compare against null: ");
            }

            if(left instanceof Integer && right instanceof Integer) {
                return (Integer) left > (Integer) right;
            } else if(left instanceof Number && right instanceof Number) {
                return ((Number) left).doubleValue() > ((Number) right).doubleValue();
            } else if(left instanceof String && right instanceof String) {
                return ((String) left).compareTo((String) right) > 0;
            } else {
                throw new RuntimeException("Cannot compare two different values: " + left.getClass() + " > " + right.getClass());
            }
        });

        libraryScope.putFunc(">=", (scope, args) -> {

            validateArgs(">=", args, 2);

            Object left = args.get(0);
            Object right = args.get(1);

            if(left == null || right == null) {
                throw new RuntimeException("Cannot compare against null: ");
            }

            if(left instanceof Integer && right instanceof Integer) {
                return (Integer) left >= (Integer) right;
            } else if(left instanceof Number && right instanceof Number) {
                return ((Number) left).doubleValue() >= ((Number) right).doubleValue();
            } else if(left instanceof String && right instanceof String) {
                return ((String) left).compareTo((String) right) >= 0;
            } else {
                throw new RuntimeException("Cannot compare two different values: " + left.getClass() + " >= " + right.getClass());
            }
        });

        libraryScope.putFunc("?", (scope, args) -> {

            validateArgs("?", args, 1);

            Object value = args.get(0);

            return value != null;
        });

        libraryScope.putFunc("!", (scope, args) -> {

            validateArgs("!", args, 1);

            Object value = args.get(0);

            return value == null || value == Boolean.FALSE;
        });

        libraryScope.putFunc("print", (scope, args) -> {

            String output = args.mkString();

            System.out.println(output);

            return output;
        });

        libraryScope.putFunc("&", (scope, args) -> args.mkString());
    }

    public void buildTypes(TypeScope typeScope) {
        typeScope.declareType("_", LiteralTypeToken.nullTypeToken);
        typeScope.declareType("true", LiteralTypeToken.booleanTypeToken);
        typeScope.declareType("false", LiteralTypeToken.booleanTypeToken);
        typeScope.declareType("print", new SimpleFunctionTypeToken(List.of(LiteralTypeToken.stringTypeToken), LiteralTypeToken.unitTypeToken));

        var leftOpType = new ParamaterTypeToken("Left");
        var rightOpType = new ParamaterTypeToken("Right");
        typeScope.declareType("&", new GenericFunctionTypeToken(List.of(leftOpType, rightOpType), List.of(leftOpType, rightOpType), LiteralTypeToken.stringTypeToken));

        var opIntInt = new SimpleFunctionTypeToken(List.of(LiteralTypeToken.intTypeToken, LiteralTypeToken.intTypeToken), LiteralTypeToken.intTypeToken);
        var opIntFloat = new SimpleFunctionTypeToken(List.of(LiteralTypeToken.intTypeToken, LiteralTypeToken.floatTypeToken), LiteralTypeToken.floatTypeToken);
        var opFloatInt = new SimpleFunctionTypeToken(List.of(LiteralTypeToken.floatTypeToken, LiteralTypeToken.intTypeToken), LiteralTypeToken.floatTypeToken);
        var opFloatFloat = new SimpleFunctionTypeToken(List.of(LiteralTypeToken.floatTypeToken, LiteralTypeToken.floatTypeToken), LiteralTypeToken.floatTypeToken);
        var opNum = new OverloadedFunctionTypeToken(List.of(opIntInt, opIntFloat, opFloatInt, opFloatFloat));

        typeScope.declareType("+", new OverloadedFunctionTypeToken(List.of(opIntInt, opIntFloat, opFloatInt, opFloatFloat)));

        typeScope.declareType("-", opNum);
        typeScope.declareType("*", opNum);

        var opIntIntToFloat = new SimpleFunctionTypeToken(List.of(LiteralTypeToken.intTypeToken, LiteralTypeToken.intTypeToken), LiteralTypeToken.floatTypeToken);

        typeScope.declareType("/", new OverloadedFunctionTypeToken(List.of(opIntIntToFloat, opIntFloat, opFloatInt, opFloatFloat)));
        typeScope.declareType("**", opNum);

        typeScope.declareType("!", new SimpleFunctionTypeToken(List.of(LiteralTypeToken.booleanTypeToken), LiteralTypeToken.booleanTypeToken));

        var param = new ParamaterTypeToken("Type");

        typeScope.declareType("?", new GenericFunctionTypeToken(List.of(param), List.of(param), LiteralTypeToken.booleanTypeToken));

        var equality = new GenericFunctionTypeToken(List.of(param), List.of(param, param), LiteralTypeToken.booleanTypeToken);

        typeScope.declareType("==", equality);
        typeScope.declareType("!=", equality);

        var intCompare = new SimpleFunctionTypeToken(List.of(LiteralTypeToken.intTypeToken, LiteralTypeToken.intTypeToken), LiteralTypeToken.booleanTypeToken);
        var floatCompare = new SimpleFunctionTypeToken(List.of(LiteralTypeToken.floatTypeToken, LiteralTypeToken.floatTypeToken), LiteralTypeToken.booleanTypeToken);
        var numCompare = new OverloadedFunctionTypeToken(List.of(intCompare, floatCompare));
        typeScope.declareType("<", numCompare);
        typeScope.declareType(">", numCompare);
        typeScope.declareType(">=", numCompare);
        typeScope.declareType("=<", numCompare);

        typeScope.declareType("List", LiteralTypeToken.listTypeToken.getBase());
        typeScope.declareType("Map", LiteralTypeToken.mapTypeToken.getBase());
        typeScope.declareType("String", LiteralTypeToken.stringTypeToken);
        typeScope.declareType("Int", LiteralTypeToken.intTypeToken);
        typeScope.declareType("Float", LiteralTypeToken.floatTypeToken);
    }

    private void validateArgs(String op, List<?> args, int values) {
        int size = args.size();

        if (!(size == values)) {
            throw new RuntimeException("Wrong number of arguments for function '" + op + "', found: " + args);
        }
    }

    private void validateArgs(String op, List<Object> args, int min, int max) {
        int size = args.size();

        if (size < min || size > max) {
            throw new RuntimeException("Wrong number of arguments for function '" + op + "', found: " + args);
        }
    }
}
