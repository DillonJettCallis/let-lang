package com.redgear.let.eval;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by LordBlackHole on 2017-01-08.
 */
public class ListLibrary implements ModuleDefinition {

    @Override
    public String getName() {
        return "List";
    }

    @Override
    public void buildLibrary(ModuleScope moduleScope) {
        moduleScope.putFunc("build", (scope, args) -> args);

        moduleScope.putFunc("get", (scope, args) -> {

            if(args.size() != 2) {
                throw new RuntimeException("Wrong number of arguments for function 'List.get', found: " + args);
            }

            Object first = args.get(0);
            Object second = args.get(1);

            if(first instanceof List && second instanceof Number) {
                return ((List) first).get(((Number) second).intValue());
            } else {
                throw new RuntimeException("Illegal argument for function List.get: Expected List, found: " + (first == null ? "null" : first.getClass()));
            }


        });

        moduleScope.putFunc("forEach", (scope, args) -> {

            if(args.size() != 2) {
                throw new RuntimeException("Wrong number of arguments for function 'List.forEach', found: " + args);
            }

            Object first = args.get(0);
            Object second = args.get(1);

            if(first instanceof List && second instanceof Func) {
                List<?> list = ((List) first);
                Func func = (Func) second;

                list.forEach(obj -> Caller.callEvaluated(scope, func, Collections.singletonList(obj)));

                return null;
            } else {
                throw new RuntimeException("Illegal arguments: Expected (list, func) found: (" + first + ", " + second + ")");
            }

        });

        moduleScope.putFunc("map", (scope, args) -> {

            if(args.size() != 2) {
                throw new RuntimeException("Wrong number of arguments for function 'List.map', found: " + args);
            }

            Object first = args.get(0);
            Object second = args.get(1);

            if(first instanceof List && second instanceof Func) {
                List<?> list = ((List) first);
                Func func = (Func) second;

                return list.stream().map(obj -> Caller.callEvaluated(scope, func, Collections.singletonList(obj))).collect(Collectors.toList());
            } else {
                throw new RuntimeException("Illegal arguments: Expected (list, func) found: (" + first + ", " + second + ")");
            }

        });
    }
}
