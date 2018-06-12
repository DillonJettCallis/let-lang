package com.redgear.let.lib;

import com.redgear.let.ast.Expression;
import com.redgear.let.ast.Variable;
import com.redgear.let.eval.*;
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

        libraryScope.putMacroFunc("&&", (scope, args) -> {

            if(args.size() != 2)
                throw new RuntimeException("Wrong number of arguments for op '&&', found: " + args);

            Expression left = args.get(0);
            Expression right = args.get(1);

            Object first = interpreter.eval(scope, left);

            if(first == null || first == Boolean.FALSE) {
                return false;
            } else {
                Object second = interpreter.eval(scope, right);

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

            Object first = interpreter.eval(scope, left);

            if(first == null || first == Boolean.FALSE) {
                Object second = interpreter.eval(scope, right);

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

            Object test = interpreter.eval(scope, args.get(0));

            if (test != null && test != Boolean.FALSE) {
                return interpreter.eval(scope, args.get(1));
            } else if(argSize == 3) {
                return interpreter.eval(scope,  args.get(2));
            } else {
                return null;
            }
        });

        libraryScope.putMacroFunc(".", (scope, args) -> {

            validateArgs(".", args, 2);

            Object left = interpreter.eval(scope, args.get(0));
            Object right = interpreter.eval(scope, args.get(1));

            if(left instanceof ModuleScope && right instanceof String) {
                return ((ModuleScope) left).getValue((String) right);
            } else if (left == null) {
                Expression module = args.get(0);

                if (module instanceof Variable) {
                    String name = ((Variable) module).getName();

                    throw new RuntimeException("Missing import for module: " + name + " " + module.getLocation().print());
                } else {
                    throw new RuntimeException("Missing module " + module.getLocation().print());
                }

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

        libraryScope.putFunc("$buildList", (scope, args) -> List.ofAll(args));
        libraryScope.putFunc("$buildMap", (scope, args) -> args.sliding(2, 2).toMap(pair -> Tuple.of(pair.get(0), pair.get(1))));

        libraryScope.putFunc("print", (scope, args) -> {

            String output = args.mkString();

            System.out.println(output);

            return output;
        });
    }

    public void buildTypes(TypeScope typeScope) {
        typeScope.declareType("_", LiteralTypeToken.nullTypeToken);
        typeScope.declareType("true", LiteralTypeToken.booleanTypeToken);
        typeScope.declareType("false", LiteralTypeToken.booleanTypeToken);
        typeScope.declareType("print", new DynamicFunctionTypeToken("print", args -> LiteralTypeToken.stringTypeToken));

        var opIntInt = new SimpleFunctionTypeToken(List.of(LiteralTypeToken.intTypeToken, LiteralTypeToken.intTypeToken), LiteralTypeToken.intTypeToken);
        var opIntFloat = new SimpleFunctionTypeToken(List.of(LiteralTypeToken.intTypeToken, LiteralTypeToken.floatTypeToken), LiteralTypeToken.floatTypeToken);
        var opFloatInt = new SimpleFunctionTypeToken(List.of(LiteralTypeToken.floatTypeToken, LiteralTypeToken.intTypeToken), LiteralTypeToken.floatTypeToken);
        var opFloatFloat = new SimpleFunctionTypeToken(List.of(LiteralTypeToken.floatTypeToken, LiteralTypeToken.floatTypeToken), LiteralTypeToken.floatTypeToken);
        var opNum = new OverloadedFunctionTypeToken(List.of(opIntInt, opIntFloat, opFloatInt, opFloatFloat));

        var opString = new SimpleFunctionTypeToken(List.of(LiteralTypeToken.stringTypeToken, LiteralTypeToken.stringTypeToken), LiteralTypeToken.stringTypeToken);

        typeScope.declareType("+", new OverloadedFunctionTypeToken(List.of(opString, opIntInt, opIntFloat, opFloatInt, opFloatFloat)));

        typeScope.declareType("-", opNum);
        typeScope.declareType("*", opNum);

        var opIntIntToFloat = new SimpleFunctionTypeToken(List.of(LiteralTypeToken.intTypeToken, LiteralTypeToken.intTypeToken), LiteralTypeToken.floatTypeToken);

        typeScope.declareType("/", new OverloadedFunctionTypeToken(List.of(opIntIntToFloat, opIntFloat, opFloatInt, opFloatFloat)));
        typeScope.declareType("**", opNum);

        var boolOp = binaryOp(LiteralTypeToken.booleanTypeToken);

        typeScope.declareType("&&", boolOp);
        typeScope.declareType("||", boolOp);
        typeScope.declareType("!", new SimpleFunctionTypeToken(List.of(LiteralTypeToken.booleanTypeToken), LiteralTypeToken.booleanTypeToken));
        typeScope.declareType("?", new DynamicFunctionTypeToken("?", args -> {
            if (args.size() != 1) {
                return null;
            } else {
                return LiteralTypeToken.booleanTypeToken;
            }
        }));

        typeScope.declareType("==", new DynamicFunctionTypeToken("==", args -> {
            if (args.size() != 2) {
                return null;
            } else {
                return LiteralTypeToken.booleanTypeToken;
            }
        }));
        typeScope.declareType("!=", new DynamicFunctionTypeToken("==", args -> {
            if (args.size() != 2) {
                return null;
            } else {
                return LiteralTypeToken.booleanTypeToken;
            }
        }));
        typeScope.declareType("<", new DynamicFunctionTypeToken("<", this::compareOp));
        typeScope.declareType(">", new DynamicFunctionTypeToken(">", this::compareOp));
        typeScope.declareType(">=", new DynamicFunctionTypeToken(">=", this::compareOp));
        typeScope.declareType("=<", new DynamicFunctionTypeToken("=<", this::compareOp));

        typeScope.declareType("if", new DynamicFunctionTypeToken("if", args -> {
            if (args.size() != 2 && args.size() != 3) {
                return null;
            } else {
                var condition = args.get(0);
                if (condition == LiteralTypeToken.booleanTypeToken) {
                    var thenBlock = args.get(1);

                    if (args.size() == 3) {
                        var elseBlock = args.get(2);
                        if (thenBlock == elseBlock) {
                            return thenBlock;
                        } else {
                            return null;
                        }
                    } else {
                        return thenBlock;
                    }
                } else {
                    return null;
                }
            }
        }));

        typeScope.declareType("List", LiteralTypeToken.listTypeToken);
        typeScope.declareType("Map", LiteralTypeToken.mapTypeToken);

        typeScope.declareType("$buildList", new DynamicFunctionTypeToken("$buildList", ListLibrary::buildList));
        typeScope.declareType("$buildMap", new DynamicFunctionTypeToken("$buildMap", MapLibrary::buildMap));
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

    private TypeToken binaryNumericOp(List<TypeToken> args) {
        if (args.size() != 2) {
            return null;
        } else {
            var first = args.get(0);
            var second = args.get(0);

            return numericOp(first, second);
        }
    }

    private TypeToken numericOp(TypeToken first, TypeToken second) {
        if (first == LiteralTypeToken.intTypeToken && second == LiteralTypeToken.intTypeToken) {
            return LiteralTypeToken.intTypeToken;
        } else if ((first == LiteralTypeToken.intTypeToken || first == LiteralTypeToken.floatTypeToken) && (second == LiteralTypeToken.intTypeToken || second == LiteralTypeToken.floatTypeToken)) {
            return LiteralTypeToken.floatTypeToken;
        } else {
            return null;
        }
    }

    private TypeToken compareOp(List<TypeToken> args) {
        if (args.size() != 2) {
            return null;
        } else {
            var first = args.get(0);
            var second = args.get(0);

            if (first == LiteralTypeToken.stringTypeToken || second == LiteralTypeToken.stringTypeToken) {
                return LiteralTypeToken.booleanTypeToken;
            } else if ((first == LiteralTypeToken.intTypeToken || first == LiteralTypeToken.floatTypeToken) && (second == LiteralTypeToken.intTypeToken || second == LiteralTypeToken.floatTypeToken)) {
                return LiteralTypeToken.booleanTypeToken;
            } else {
                return null;
            }
        }
    }

    private TypeToken binaryOp(TypeToken source) {
        return new SimpleFunctionTypeToken(List.of(source, source), source);
    }

}
