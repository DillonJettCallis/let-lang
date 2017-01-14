package com.redgear.let.eval;

import javaslang.collection.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by LordBlackHole on 2017-01-08.
 */
public class ListLibrary implements ModuleDefinition {

    private static final Logger log = LoggerFactory.getLogger(ListLibrary.class);

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

                list.forEach(obj -> Caller.callEvaluated(scope, func, List.of(obj)));

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

                return list.map(obj -> Caller.callEvaluated(scope, func, List.of(obj)));
            } else {
                throw new RuntimeException("Illegal arguments: Expected (list, func) found: (" + first + ", " + second + ")");
            }

        });

        moduleScope.putFunc("fold", (scope, args) -> {

            if (args.size() > 3 || args.size() == 0) {
                throw new RuntimeException("Wrong number of arguments for function 'List.fold', found: " + args);
            }

            Object first = args.get(0);
            Object second = args.get(1);

            if (first instanceof List) {
                List<?> list = (List) first;

                Object third = args.get(2);

                if (third instanceof Func) {
                    Func func = (Func) third;

                    Object result = second;

                    for (Object item : list) {
                        result = Caller.callEvaluated(scope, func, List.of(result, item));
                    }

                    return result;
                } else {
                    throw new RuntimeException("Illegal arguments: Expected Function found: " + (third == null ? "null" : third.getClass()));
                }

            } else {
                throw new RuntimeException("Illegal arguments: Expected list found: " + (first == null ? "null" : first.getClass()));
            }


        });
    }
}
