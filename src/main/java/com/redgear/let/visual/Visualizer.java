package com.redgear.let.visual;

import com.redgear.let.ast.*;
import com.redgear.let.walk.PrintingWalker;

import java.io.Writer;

/**
 * Created by LordBlackHole on 2016-12-30.
 */
public class Visualizer extends PrintingWalker {


    public Visualizer(Writer writer) {
        super(writer);
    }

    @Override
    public void walk(Assignment assignment) {
        print(printIndent(), "Assignment: \n");
        indent++;

        walk(assignment.getVar());

        walk(assignment.getExp());

        indent--;
    }

    @Override
    public void walk(Literal literal) {
        Object value = literal.getValue();

        if(value instanceof String) {
            print(printIndent(), "Literal: \"", value.toString(), "\"\n");
        } else {
            print(printIndent(), "Literal: ", String.valueOf(value), "\n");
        }
    }

    public void walk(Module module) {
        print(printIndent(), "Module: \n");
        indent++;
        module.getExpressions().forEach(this::walk);
        indent--;
    }

    @Override
    public void walk(Variable variable) {
        print(printIndent(), "Variable: ", variable.getName(), "\n");
    }

    @Override
    public void walk(Call call) {
        String modName = ""; // call.getModuleRef().getName();

        if(modName == null) {
            modName = "";
        } else {
            modName += ".";
        }

        print(printIndent(), "Call: ", modName, call.getName(), "\n");
        indent++;

        call.getArguments().forEach(this::walk);

        indent--;
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
