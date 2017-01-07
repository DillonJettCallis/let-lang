package com.redgear.let.format;

import com.redgear.let.ast.*;
import com.redgear.let.walk.PrintingWalker;

import java.io.Writer;

/**
 * Created by LordBlackHole on 2016-12-30.
 */
public class Formatter extends PrintingWalker {

    public Formatter(Writer writer) {
        super(writer);
    }

    @Override
    public void walk(Assignment assignment) {
        print(printIndent(), "let ", assignment.getVar().getName(), " = ");
        walk(assignment.getExp());
        print(";\n");
    }

    @Override
    public void walk(Literal literal) {
        Object value = literal.getValue();

        if(value instanceof String) {
            print("\"", value.toString(), "\"");
        } else {
            print(String.valueOf(value));
        }
    }

    public void walk(Module module) {
        module.getExpressions().forEach(this::walk);
    }

    @Override
    public void walk(Variable variable) {
        print(variable.getName());
    }

    @Override
    public void walk(Call call) {
        String modName = "";// call.getModuleRef().getName();

        if(modName == null) {
            modName = "";
        } else {
            modName += ".";
        }

        print(modName);
        walk(call.getMethod());
        print("(");

        if(call.getArguments().size() == 0) {
            print(")");
        } else {
            walk(call.getArguments().get(0));

            call.getArguments().stream().skip(1).forEach(arg -> {

                print(", ");
                walk(arg);

            });

            print(")");
        }
    }

    @Override
    public void walk(Func func) {

    }

    @Override
    public void walk(Import im) {

    }

    @Override
    public void walk(Export ex) {

    }


}
