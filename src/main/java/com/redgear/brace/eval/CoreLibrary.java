package com.redgear.brace.eval;

import com.redgear.brace.ast.Expression;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by LordBlackHole on 2017-01-02.
 */
public class CoreLibrary {


    public void buildLibrary(LibraryScope libraryScope) {
        libraryScope.putFunc("+", (scope, args) -> {

            validateArgs("+", args, 2);

            Object left = args.get(0);
            Object right = args.get(1);

            if(left instanceof String || right instanceof String) {
                return String.valueOf(left) + String.valueOf(right);
            } else if (left instanceof Integer && right instanceof Integer) {
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


        libraryScope.putFunc("!", (scope, args) -> {

            validateArgs("!", args, 1);

            Object value = args.get(0);

            return value == null || value == Boolean.FALSE;
        });

        libraryScope.putFunc("++", (scope, args) -> {

            validateArgs("++", args, 1);

            Object value = args.get(0);

            if(value instanceof Integer) {
                return (Integer) value + 1;
            } else if (value instanceof Double) {
                return (Double) value + 1.0;
            } else {
                throw new RuntimeException("Illegal arguments to '++' op, found: " + args);
            }
        });

        libraryScope.putFunc("--", (scope, args) -> {

            validateArgs("--", args, 1);

            Object value = args.get(0);

            if(value instanceof Integer) {
                return (Integer) value - 1;
            } else if (value instanceof Double) {
                return (Double) value - 1.0;
            } else {
                throw new RuntimeException("Illegal arguments to '--' op, found: " + args);
            }
        });

        libraryScope.putFunc("print", (scope, args) -> {

            System.out.println(args.stream().map(String::valueOf).collect(Collectors.joining()));

            return null;
        });
    }

    private void validateArgs(String op, List<Object> args, int values) {
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
