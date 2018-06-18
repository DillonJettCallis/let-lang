package com.redgear.let.lib;

import com.redgear.let.eval.Interpreter;
import com.redgear.let.eval.Scope;
import com.redgear.let.types.*;
import javaslang.collection.List;

public class TupleLibrary implements ModuleDefinition {
    @Override
    public String getName() {
        return "Core.Tuple";
    }

    @Override
    public void buildLibrary(Interpreter interpreter, Scope moduleScope) {
        moduleScope.putFunc("head", (scope, args) -> {
            if (args.size() == 1) {
                Object first = args.head();

                if (first instanceof List) {
                    return ((List) first).head();
                } else {
                    throw new RuntimeException("Invalid arguments passed to Core.Tuple.head. Expected Tuple, found: " + first);
                }
            } else {
                throw new RuntimeException("Wrong number of arguments arguments passed to Core.Tuple.head. Expected Tuple, found: " + args);
            }
        });

        moduleScope.putFunc("tail", (scope, args) -> {
            if (args.size() == 1) {
                Object first = args.head();

                if (first instanceof List) {
                    return ((List) first).tail();
                } else {
                    throw new RuntimeException("Invalid arguments passed to Core.Tuple.tail. Expected Tuple, found: " + first);
                }
            } else {
                throw new RuntimeException("Wrong number of arguments arguments passed to Core.Tuple.tail. Expected Tuple, found: " + args);
            }
        });
    }

    @Override
    public void buildTypes(TypeScope moduleTypeScope) {
        var t1 = new ParamaterTypeToken("T1");
        var t2 = new ParamaterTypeToken("T2");
        var t3 = new ParamaterTypeToken("T3");
        var t4 = new ParamaterTypeToken("T4");
        var t5 = new ParamaterTypeToken("T5");

        var head2 = new GenericFunctionTypeToken(List.of(t1, t2), List.of(new GenericTypeToken(LiteralTypeToken.tupleTypeToken, List.of(t1, t2))), t1);
        var head3 = new GenericFunctionTypeToken(List.of(t1, t2, t3), List.of(new GenericTypeToken(LiteralTypeToken.tupleTypeToken, List.of(t1, t2, t3))), t1);
        var head4 = new GenericFunctionTypeToken(List.of(t1, t2, t3, t4), List.of(new GenericTypeToken(LiteralTypeToken.tupleTypeToken, List.of(t1, t2, t3, t4))), t1);
        var head5 = new GenericFunctionTypeToken(List.of(t1, t2, t3, t4, t5), List.of(new GenericTypeToken(LiteralTypeToken.tupleTypeToken, List.of(t1, t2, t3, t4, t5))), t1);

        moduleTypeScope.declareType("head", new OverloadedFunctionTypeToken(List.of(head2, head3, head4, head5)));

        var tail2 = new GenericFunctionTypeToken(List.of(t1, t2), List.of(new GenericTypeToken(LiteralTypeToken.tupleTypeToken, List.of(t1, t2))), t2);
        var tail3 = new GenericFunctionTypeToken(List.of(t1, t2, t3), List.of(new GenericTypeToken(LiteralTypeToken.tupleTypeToken, List.of(t1, t2, t3))), new GenericTypeToken(LiteralTypeToken.tupleTypeToken, List.of(t2, t3)));
        var tail4 = new GenericFunctionTypeToken(List.of(t1, t2, t3, t4), List.of(new GenericTypeToken(LiteralTypeToken.tupleTypeToken, List.of(t1, t2, t3, t4))), new GenericTypeToken(LiteralTypeToken.tupleTypeToken, List.of(t2, t3, t4)));
        var tail5 = new GenericFunctionTypeToken(List.of(t1, t2, t3, t4, t5), List.of(new GenericTypeToken(LiteralTypeToken.tupleTypeToken, List.of(t1, t2, t3, t4, t5))), new GenericTypeToken(LiteralTypeToken.tupleTypeToken, List.of(t2, t3, t4, t5)));

        moduleTypeScope.declareType("tail", new OverloadedFunctionTypeToken(List.of(tail2, tail3, tail4, tail5)));
    }
}
