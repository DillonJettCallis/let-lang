package com.redgear.let.lib;

import com.redgear.let.eval.Interpreter;
import com.redgear.let.eval.Scope;
import com.redgear.let.types.TypeScope;

public interface ModuleDefinition {

    String getName();

    void buildLibrary(Interpreter interpreter, Scope moduleScope);

    void buildTypes(TypeScope moduleTypeScope);
}
