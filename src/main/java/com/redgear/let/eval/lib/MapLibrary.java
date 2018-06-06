package com.redgear.let.eval.lib;


import com.redgear.let.eval.ModuleDefinition;
import com.redgear.let.eval.ModuleScope;
import javaslang.Tuple;
import javaslang.collection.Map;

/**
 * Created by LordBlackHole on 2017-01-08.
 */
public class MapLibrary implements ModuleDefinition {

    @Override
    public String getName() {
        return "Map";
    }

    public void buildLibrary(ModuleScope moduleScope) {

        moduleScope.putFunc("build", (scope, args) -> args.sliding(2, 2).toMap(pair -> Tuple.of(pair.get(0), pair.get(1))));

        moduleScope.putFunc("get", (scope, args) -> {

            if(args.size() != 2) {
                throw new RuntimeException("Wrong number of arguments for function 'Map.get', found: " + args);
            }

            Object first = args.get(0);
            Object second = args.get(1);

            if(first instanceof Map) {
                return ((Map) first).get(second).getOrElse((Object) null);
            } else {
                throw new RuntimeException("Illegal argument for function Map.get: Expected Map, found: " + (first == null ? "null" : first.getClass()));
            }
        });
    }
}
