package com.redgear.let.compile2js;

import com.redgear.let.ast.Expression;

public interface Compiler {

    void compile(SafeWriter writer, Expression expression);
    
}
