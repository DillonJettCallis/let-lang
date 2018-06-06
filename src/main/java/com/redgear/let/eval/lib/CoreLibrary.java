package com.redgear.let.eval.lib;

import com.redgear.let.ast.Expression;
import com.redgear.let.eval.Interpreter;
import com.redgear.let.eval.LibraryScope;
import com.redgear.let.eval.ModuleScope;
import javaslang.collection.List;

import java.util.Objects;

/**
 * Created by LordBlackHole on 2017-01-02.
 */
public class CoreLibrary {


    private final Interpreter interpreter;

    public CoreLibrary(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public void buildLibrary(LibraryScope libraryScope) {
        libraryScope.putValue("_", null);
        libraryScope.putValue("true", true);
        libraryScope.putValue("false", false);

        libraryScope.putMacroFunc("&&", (scope, args) -> {

            if(args.size() != 2)
                throw new RuntimeException("Wrong number of arguments for op '&&', found: " + args);

            Expression left = args.get(0);
            Expression right = args.get(1);

            Object first = left.eval(scope);

            if(first == null || first == Boolean.FALSE) {
                return false;
            } else {
                Object second = right.eval(scope);

                if(second == null || second == Boolean.FALSE){
                    return false;
                } else {
                    return second;
                }
            }

        });

        libraryScope.putMacroFunc("||", (scope, args) -> {

            if(args.size() != 2)
                throw new RuntimeException("Wrong number of arguments for op '||', found: " + args);

            Expression left = args.get(0);
            Expression right = args.get(1);

            Object first = left.eval(scope);

            if(first == null || first == Boolean.FALSE) {
                Object second = right.eval(scope);

                if(second == null || second == Boolean.FALSE){
                    return false;
                } else {
                    return second;
                }
            } else {
                return first;
            }

        });

        libraryScope.putMacroFunc("if", (scope, args) -> {
            int argSize = args.size();

            if(argSize != 2 && argSize != 3) {
                throw new RuntimeException("Wrong number of arguments for if statement! Must have 2 or 3, found: " + argSize);
            }

            Object test = args.get(0).eval(scope);

            if (test != null && test != Boolean.FALSE) {
                return args.get(1).eval(scope);
            } else if(argSize == 3) {
                return args.get(2).eval(scope);
            } else {
                return null;
            }
        });

        libraryScope.putFunc(".", (scope, args) -> {

            validateArgs(".", args, 2);

            Object left = args.get(0);
            Object right = args.get(1);

            if(left instanceof ModuleScope && right instanceof String) {
                return ((ModuleScope) left).getValue((String) right);
            } else {
                throw new RuntimeException("Invalid module access: " + left.getClass());
            }

        });

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

        libraryScope.putFunc("<=", (scope, args) -> {

            validateArgs("<=", args, 2);

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
