package com.redgear.let.eval;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LordBlackHole on 2017-01-08.
 */
public class MapLibrary implements ModuleDefinition{

    @Override
    public String getName() {
        return "Map";
    }

    public void buildLibrary(ModuleScope moduleScope) {

        moduleScope.putFunc("build", (scope, args) -> {

            Map<Object, Object> map = new HashMap<>();

            for(int i = 0; i < args.size(); i+=2) {
                map.put(args.get(i), args.get(i + 1));
            }

            return map;
        });

        moduleScope.putFunc("get", (scope, args) -> {

            if(args.size() != 2) {
                throw new RuntimeException("Wrong number of arguments for function 'Map.get', found: " + args);
            }

            Object first = args.get(0);
            Object second = args.get(1);

            if(first instanceof Map) {
                return ((Map) first).get(second);
            } else {
                throw new RuntimeException("Illegal argument for function Map.get: Expected Map, found: " + (first == null ? "null" : first.getClass()));
            }


        });
    }



}
