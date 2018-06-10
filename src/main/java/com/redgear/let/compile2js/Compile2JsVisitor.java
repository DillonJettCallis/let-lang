package com.redgear.let.compile2js;

import com.redgear.let.ast.*;
import com.redgear.let.tools.SafeWriter;

import java.io.Writer;

import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.Predicates.instanceOf;

public class Compile2JsVisitor implements AstVisitor {

    private final SafeWriter writer;

    public Compile2JsVisitor(Writer writer) {
        this.writer = new SafeWriter(writer, this);
    }


    public Void visit(Assignment ex) {
        writer.append("const ", ex.getVar().getName(), " = ")
                .append(ex.getBody())
                .append(";");
        return null;
    }

    public Void visit(Call ex) {
        if (ex.getName().equals("if")) {
            compileIf(ex);
        } else if (ex.getName().equals(".")) {
            compileAccess(ex);
        } else if (ex.getName().matches("^\\W+$")) {
            compileOp(ex);
        } else {
            writer.append(ex.getMethod())
                    .append("(");
            for (int i = 0; i < ex.getArguments().size(); i++) {
                writer.append(ex.getArguments().get(i));
                if (i != ex.getArguments().size() - 1) {
                    writer.append(",");
                }
            }

            writer.append(")");


        }
        return null;
    }

    private Void compileIf(Call ex) {
        writer.append("((")
                .append(ex.getArguments().get(0))
                .append(")?(")
                .append(ex.getArguments().get(1))
                .append(")")
                .append(":(")
                .append(ex.getArguments().getOrElse(new Variable(null, null, "_")))
                .append("))");

        return null;
    }

    private Void compileAccess(Call ex) {
        writer.append("((")
                .append(ex.getArguments().get(0))
                .append(")[")
                .append(ex.getArguments().get(1))
                .append("])");

        return null;
    }

    private Void compileOp(Call ex) {
        if (ex.getArguments().size() == 2) {
            writer.append("((")
                    .append(ex.getArguments().get(0))
                    .append(")")
                    .append(ex.getName())
                    .append("(")
                    .append(ex.getArguments().get(1))
                    .append("))");
        } else {
            writer.append("(")
                    .append(ex.getName())
                    .append(ex.getArguments().get(0))
                    .append(")");
        }

        return null;
    }

    public Void visit(Export ex) {
        writer.append("export const ", ex.getName(), " = ")
                .append(ex.getExpression())
                .append(";");
        return null;
    }

    public Void visit(Import ex) {
        writer.append("import * as ", ex.getAlias(), " from '", ex.getPath(), "';");
        return null;
    }

    public Void visit(Lambda ex) {
        writer.append("((");
        for (int i = 0; i < ex.getVariables().size(); i++) {
            writer.append(ex.getVariables().get(i));
            if (i != ex.getVariables().size() - 1) {
                writer.append(",");
            }
        }
        writer.append(") => {");
        for (Expression next : ex.getStatements()) {
            writer.append(next).append(";");
        }
        writer.append("})");
        return null;
    }

    public Void visit(Literal ex) {
        Match(ex.getValue()).of(
                Case(instanceOf(String.class), value -> writer.append("'", value, "'")),
                Case(instanceOf(Integer.class), value -> writer.append(String.valueOf(value))),
                Case(instanceOf(Double.class), value -> writer.append(String.valueOf(value)))
        );
        return null;
    }

    public Void visit(com.redgear.let.ast.Module ex) {
        for (Expression next : ex.getExpressions()) {
            writer.append(next)
                    .append(";");
        }
        return null;
    }

    public Void visit(Parenthesized ex) {
        writer.append("(");
        for (int i = 0; i < ex.getExpressions().size(); i++) {
            writer.append(ex.getExpressions().get(i));
            if (i != ex.getExpressions().size() - 1) {
                writer.append(",");
            }
        }
        writer.append(")");
        return null;
    }

    public Void visit(Variable ex) {
        if (ex.getName().equals("_")) {
            writer.append("undefined");
        } else {
            writer.append(ex.getName());
        }
        return null;
    }

}
