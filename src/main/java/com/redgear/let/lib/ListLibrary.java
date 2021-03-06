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
            if(args.size() == 3){
                Object first = args.get(0);
                Object second = args.get(1);
                Object third = args.get(2);

                if (first instanceof List && third instanceof Func) {
                    List<Object> list = (List) first;
                    Func func = (Func) third;

                    return list.foldRight(second, (l, r) -> caller.callEvaluated(scope, func, List.of(l, r)));
                }
            } else if (args.size() == 2) {
                Object first = args.get(0);
                Object second = args.get(1);

                if (first instanceof List && second instanceof List) {
                    List<Object> list = (List) first;
                    List tuple = (List) second;

                    if (tuple.size() == 2) {
                        Object tupleFirst = tuple.get(0);
                        Object tupleSecond = tuple.get(1);

                        if (tupleSecond instanceof Func) {
                            Func func = (Func) tupleSecond;

                            return list.foldRight(tupleFirst, (l, r) -> caller.callEvaluated(scope, func, List.of(l, r)));
                        }
                    }
                } else if (first instanceof List && second instanceof Func) {
                    List<Object> list = (List) first;
                    Func func = (Func) second;

                    return list.tail().foldRight(list.head(), (l, r) -> caller.callEvaluated(scope, func, List.of(l, r)));
                }
            } else {
                throw new RuntimeException("Wrong number of arguments for function 'List.fold', found: " + args);
            }

            throw new RuntimeException("Illegal arguments: Expected (list, start, func) | (list, (start, func)) found: (" + args + ")");
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
        var listItem = new ParamaterTypeToken("Item");
        var listParams = List.of(listItem);
        var listOfItem = LiteralTypeToken.listTypeToken.construct(List.of(listItem));

        typeScope.declareType("get", new GenericFunctionTypeToken(listParams, List.of(listOfItem, LiteralTypeToken.intTypeToken), listItem));

        typeScope.declareType("forEach", new GenericFunctionTypeToken(listParams, List.of(listOfItem, new GenericFunctionTypeToken(listParams, List.of(listItem), LiteralTypeToken.unitTypeToken)), LiteralTypeToken.unitTypeToken));


        var outItem = new ParamaterTypeToken("Out");
        var mapperParams = listParams.prepend(outItem);
        var mapperFunction = new GenericFunctionTypeToken(mapperParams, List.of(listItem), outItem);
        var mapperResult = LiteralTypeToken.listTypeToken.construct(List.of(outItem));

        typeScope.declareType("map", new GenericFunctionTypeToken(mapperParams, List.of(listOfItem, mapperFunction), mapperResult));

        var flatMapperFunction = new GenericFunctionTypeToken(mapperParams, List.of(listItem), mapperResult);
        typeScope.declareType("flatMap", new GenericFunctionTypeToken(mapperParams, List.of(listOfItem, flatMapperFunction), mapperResult));

        var foldMapper = new GenericFunctionTypeToken(mapperParams, List.of(outItem, listItem), outItem);
        var foldNormal = new GenericFunctionTypeToken(mapperParams, List.of(listOfItem, outItem, foldMapper), outItem);
        var foldTuple = new GenericFunctionTypeToken(mapperParams, List.of(listOfItem, new GenericTypeToken(LiteralTypeToken.tupleTypeToken, List.of(outItem, foldMapper))), outItem);
        var foldReduce = new GenericFunctionTypeToken(mapperParams, List.of(listOfItem, new GenericFunctionTypeToken(mapperParams, List.of(listItem, listItem), listItem)), listItem);

        typeScope.declareType("fold", new OverloadedFunctionTypeToken(List.of(foldNormal, foldTuple, foldReduce)));


        typeScope.declareType("reduce", new GenericFunctionTypeToken(listParams, List.of(listOfItem, new GenericFunctionTypeToken(listParams, List.of(listItem, listItem), listItem)), listItem));
    }
}
