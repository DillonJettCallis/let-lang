package com.redgear.let.ast;

import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.Predicates.instanceOf;

public interface AstVisitor {

    Void visit(Assignment ex);

    Void visit(Call ex);

    Void visit(Export ex);

    Void visit(Import ex);

    Void visit(Lambda ex);

    Void visit(Literal ex);

    Void visit(Module ex);

    Void visit(Parenthesized ex);

    Void visit(Variable ex);

    default void visit(Expression ex) {
        Match(ex).of(
                Case(instanceOf(Assignment.class), this::visit),
                Case(instanceOf(Call.class), this::visit),
                Case(instanceOf(Export.class), this::visit),
                Case(instanceOf(Import.class), this::visit),
                Case(instanceOf(Lambda.class), this::visit),
                Case(instanceOf(Literal.class), this::visit),
                Case(instanceOf(Module.class), this::visit),
                Case(instanceOf(Parenthesized.class), this::visit),
                Case(instanceOf(Variable.class), this::visit)
        );
    }
    
}
