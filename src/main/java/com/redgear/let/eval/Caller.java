package com.redgear.let.eval;

import com.redgear.let.ast.Expression;
import javaslang.collection.List;


/**
 * Created by LordBlackHole on 2017-01-08.
 */
public class Caller {


    public static Object call(LocalScope scope, Func obj, List<Expression> arguments) {

        LocalScope inner = new LocalScope(scope);

        if (obj instanceof DefinedFunc) {
            DefinedFunc func = (DefinedFunc) obj;

            List<Object> args = arguments.map(ex -> ex.eval(scope));

            return func.apply(args);
        } else if (obj instanceof LibFunc) {
            LibFunc func = (LibFunc) obj;

            List<Object> args = arguments.map(ex -> ex.eval(scope));

            return func.apply(inner, args);
        } else if (obj instanceof MacroFunc) {
            MacroFunc func = (MacroFunc) obj;

            return func.apply(inner, arguments);
        } else {
            throw new RuntimeException("Unknown function type: " + obj.getClass());
        }

    }


    public static Object callEvaluated(LocalScope scope, Func func, List<Object> args) {

        if(func instanceof DefinedFunc) {
            return ((DefinedFunc) func).apply(args);
        } else if(func instanceof LibFunc) {
            return ((LibFunc) func).apply(scope, args);
        } else if(func instanceof MacroFunc) {
            throw new IllegalStateException("Macro can't be called with already evaluated statements");
        } else {
            throw new RuntimeException("Unknown function type: " + func.getClass());
        }

    }

}
