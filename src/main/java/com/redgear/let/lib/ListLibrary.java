package com.redgear.let.lib;

import com.redgear.let.eval.*;
import com.redgear.let.types.*;
import javaslang.collection.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListLibrary implements ModuleDefinition {

    private static final Logger log = LoggerFactory.getLogger(ListLibrary.class);

    @Override
    public String getName() {
        return "Core.List";
    }

    @Override
    public void buildLibrary(Interpreter interpreter, Scope moduleScope) {
        var caller = new Caller(interpreter);

        moduleScope.putFunc("build", (scope, args) -> List.ofAll(args));

        moduleScope.putFunc("get", (scope, args) -> {

            if (args.size() != 2) {
                throw new RuntimeException("Wrong number of arguments for function 'List.get', found: " + args);
            }

            Object first = args.get(0);
            Object second = args.get(1);

            if (first instanceof List && second instanceof Number) {
                return ((List) first).get(((Number) second).intValue());
            } else {
                throw new RuntimeException("Illegal argument for function List.get: Expected List, found: " + (first == null ? "null" : first.getClass()));
            }


        });

        moduleScope.putFunc("forEach", (scope, args) -> {

            if (args.size() != 2) {
                throw new RuntimeException("Wrong number of arguments for function 'List.forEach', found: " + args);
            }

            Object first = args.get(0);
            Object second = args.get(1);

            if (first instanceof List && second instanceof Func) {
                List<?> list = ((List) first);
                Func func = (Func) second;

                list.forEach(obj -> caller.callEvaluated(scope, func, List.of(obj)));

                return null;
            } else {
                throw new RuntimeException("Illegal arguments: Expected (list, func) found: (" + first + ", " + second + ")");
            }

        });

        moduleScope.putFunc("map", (scope, args) -> {

            if (args.size() != 2) {
                throw new RuntimeException("Wrong number of arguments for function 'List.map', found: " + args);
            }

            Object first = args.get(0);
            Object second = args.get(1);

            if (first instanceof List && second instanceof Func) {
                List<?> list = ((List) first);
                Func func = (Func) second;

                return list.map(obj -> caller.callEvaluated(scope, func, List.of(obj)));
            } else {
                throw new RuntimeException("Illegal arguments: Expected (list, func) found: (" + first + ", " + second + ")");
            }

        });

        moduleScope.putFunc("flatMap", (scope, args) -> {

            if (args.size() != 2) {
                throw new RuntimeException("Wrong number of arguments for function 'List.flatMap', found: " + args);
            }

            Object first = args.get(0);
            Object second = args.get(1);

            if (first instanceof List && second instanceof Func) {
                List<?> list = ((List) first);
                Func func = (Func) second;

                return list.flatMap(obj -> (Iterable<?>) caller.callEvaluated(scope, func, List.of(obj)));
            } else {
                throw new RuntimeException("Illegal arguments: Expected (list, func) found: (" + first + ", " + second + ")");
            }

        });

        moduleScope.putFunc("fold", (scope, args) -> {

            if (args.size() > 3 || args.size() == 0) {
                throw new RuntimeException("Wrong number of arguments for function 'List.fold', found: " + args);
            }

            if(args.size() == 3){
                Object first = args.get(0);
                Object second = args.get(1);
                Object third = args.get(2);

                if (first instanceof List && third instanceof Func) {
                    List<Object> list = (List) first;
                    Func func = (Func) third;

                    return list.foldRight(second, (l, r) -> caller.callEvaluated(scope, func, List.of(l, r)));
                } else {
                    throw new RuntimeException("Illegal arguments: Expected (list, start, func) found: " + first + ", " + second + ", " + third + "}");
                }
            } else {
                Object first = args.get(0);
                Object second = args.get(1);

                if (first instanceof List && second instanceof Func) {
                    List<Object> list = (List) first;
                    Func func = (Func) second;

                    return list.tail().foldRight(list.head(), (l, r) -> caller.callEvaluated(scope, func, List.of(l, r)));
                } else {
                    throw new RuntimeException("Illegal arguments: Expected (list, func) found: " + first + ", " + second + "}");
                }
            }
        });

        moduleScope.putFunc("reduce", (scope, args) -> {

            if (args.size() > 2 || args.size() == 0) {
                throw new RuntimeException("Wrong number of arguments for function 'List.reduce', found: " + args);
            }

            Object first = args.get(0);
            Object second = args.get(1);

            if (first instanceof List && second instanceof Func) {
                List<Object> list = (List<Object>) first;
                Func func = (Func) second;

                return list.reduce((l, r) -> caller.callEvaluated(scope, func, List.of(l, r)));
            } else {
                throw new RuntimeException("Illegal arguments: Expected (list, start, func) found: " + first + ", " + second + "}");
            }
        });

    }

    @Override
    public void buildTypes(TypeScope typeScope) {
        typeScope.declareType("build", new DynamicFunctionTypeToken("build", ListLibrary::buildList));

        var listItem = new ParamaterTypeToken("Item");
        var listParams = List.of(listItem);
        var listOfItem = LiteralTypeToken.listTypeToken.construct(List.of(listItem));

        typeScope.declareType("get", new GenericFunctionTypeToken(listParams, List.of(listOfItem, LiteralTypeToken.intTypeToken), listItem));

        //TODO: Create Unit type
//        typeScope.declareType("forEach", new GenericFunctionTypeToken(listParams, List.of(new GenericFunctionTypeToken(listParams, List.of(listItem), LiteralTypeToken.nullTypeToken)), LiteralTypeToken.nullTypeToken));

        typeScope.declareType("forEach", new DynamicFunctionTypeToken("forEach", args -> {
            if (args.size() == 2) {
                var result = extractType(args);
                var second = args.get(1);

                if (result != null && second instanceof SimpleFunctionTypeToken) {
                    var func = (SimpleFunctionTypeToken) second;

                    if (func.getArgTypes().size() == 1 && func.getArgTypes().head().equals(result)) {
                        return LiteralTypeToken.nullTypeToken;
                    }
                }
            }

            return null;
        }));

        // {<Out, Item> List<Item>, {Item => Out} => List<Out>}

        var outItem = new ParamaterTypeToken("Out");
        var mapperFunction = new GenericFunctionTypeToken(listParams.prepend(outItem), List.of(listItem), outItem);
        var mapperResult = LiteralTypeToken.listTypeToken.construct(List.of(outItem));

        typeScope.declareType("map", new GenericFunctionTypeToken(listParams.prepend(outItem), List.of(listOfItem, mapperFunction), mapperResult));

        var flatMapperFunction = new GenericFunctionTypeToken(listParams.prepend(outItem), List.of(listItem), mapperResult);
        typeScope.declareType("flatMap", new GenericFunctionTypeToken(listParams.prepend(outItem), List.of(listOfItem, flatMapperFunction), mapperResult));

        typeScope.declareType("fold", new DynamicFunctionTypeToken("fold", args -> {
            if (args.size() == 3) {
                var contentType = extractType(args);
                var second = args.get(1);
                var third = args.get(2);

                if (contentType != null && third instanceof SimpleFunctionTypeToken) {
                    var func = (SimpleFunctionTypeToken) second;

                    if (func.getArgTypes().size() == 2 && func.getArgTypes().head().equals(second) && func.getArgTypes().last().equals(contentType)) {
                        return second;
                    }
                }
            }
            return null;
        }));

        typeScope.declareType("reduce", new DynamicFunctionTypeToken("reduce", args -> {
            if (args.size() == 2) {
                var contentType = extractType(args);
                var second = args.get(1);

                if (contentType != null && second instanceof SimpleFunctionTypeToken) {
                    var func = (SimpleFunctionTypeToken) second;

                    if (func.getArgTypes().size() == 2 && func.getArgTypes().head().equals(contentType) && func.getArgTypes().last().equals(contentType)) {
                        return contentType;
                    }
                }
            }
            return null;
        }));
    }

    private TypeToken extractType(List<TypeToken> args) {
        if (args.size() >= 1) {
            var head = args.head();

            if (head instanceof GenericTypeToken) {
                var gen = (GenericTypeToken) head;

                if (gen.getTypeConstructor() == LiteralTypeToken.listTypeToken.getBase()) {
                    var params = gen.getTypeParams();

                    if (params.size() == 1) {
                        return params.head();
                    } else {
                        return null;
                    }
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

    static TypeToken buildList(List<TypeToken> args) {
        var type = args.reduce((left, right) -> {
            if (left.equals(right)) {
                return left;
            } else {
                throw new RuntimeException("Lists currently only support one type at a time. Found types: " + left.getName() + " and " + right.getName());
            }
        });

        return LiteralTypeToken.listTypeToken.construct(List.of(type));
    }
}
