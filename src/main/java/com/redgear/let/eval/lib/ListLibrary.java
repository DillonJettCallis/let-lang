package com.redgear.let.eval.lib;

import com.redgear.let.eval.*;
import javaslang.collection.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by LordBlackHole on 2017-01-08.
 */
public class ListLibrary implements ModuleDefinition {

    private static final Logger log = LoggerFactory.getLogger(ListLibrary.class);

    private final Caller caller;

    public ListLibrary(Interpreter interpreter) {
        this.caller = new Caller(interpreter);
    }

    @Override
    public String getName() {
        return "List";
    }

    @Override
    public void buildLibrary(ModuleScope moduleScope) {
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

                return list;
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
}
