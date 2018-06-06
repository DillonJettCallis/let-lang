package com.redgear.let.eval.lib;

import com.redgear.let.eval.ModuleDefinition;
import com.redgear.let.eval.ModuleScope;
import javaslang.collection.List;

public class StringLibrary implements ModuleDefinition {

    @Override
    public String getName() {
        return "String";
    }

    @Override
    public void buildLibrary(ModuleScope moduleScope) {
        moduleScope.putFunc("split", (scope, args) -> {
            if (args.size() != 2) {
                throw new RuntimeException("Wrong number of arguments for function 'String.split', expected 2, found: " + args);
            }

            Object first = args.get(0);
            Object second = args.get(1);

            if (first instanceof String && second instanceof String) {
                return List.of(((String) first).split((String) second));
            } else {
                throw new RuntimeException("Illegal argument for function String.split: Expected (String, String), found: (" + (first == null ? "null" : first.getClass()) + ", " + (second == null ? "null" : second.getClass()) + ")");
            }
        });
    }
}
