package com.redgear.let;

import com.redgear.let.ast.*;
import com.redgear.let.ast.Module;
import com.redgear.let.tools.SafeWriter;

import java.io.Writer;

import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.Predicates.instanceOf;

public class AstPrinter implements AstVisitor {
    
    private final SafeWriter writer;

    public AstPrinter(Writer writer) {
        this.writer = new SafeWriter(writer, this);
    }
    
    
    private Void write(Assignment ex) {
        writer.append("{'type': 'assign', 'var': ").append(ex.getVar()).append(", 'ex': ").append(ex.getExp()).append("}");
        return null;
    }

    private Void write(Call ex) {
        writer.append("{'type': 'call', 'method': ")
                .append(ex.getMethod())
                .append(", 'args': [")
                .appendAll(ex.getArguments(), ",")
                .append("] }");
        return null;
    }

    private Void write(Export ex) {
        writer.append("{'type': 'export', 'name': '").append(ex.getName()).append("', 'value': ").append(ex.getExpression()).append("}");
        return null;
    }

    private Void write(Import ex) {
        writer.append("{'type': 'import', 'name': '").append(ex.getAlias()).append("', 'path': '").append(ex.getPath()).append("'}");
        return null;
    }

    private Void write(Lambda ex) {
        writer.append("{'type': 'lambda', 'vars': [")
                .appendAll(ex.getVariables(), ",")
                .append("], 'body': [")
                .appendAll(ex.getStatements(), ",")
                .append("]}");
        return null;
    }

    private Void write(Literal ex) {
        writer.append("{'type': 'literal', 'value': ");
        var obj = ex.getValue();

        if (obj == null) {
            writer.append("null");
        } else if (obj instanceof Integer || obj instanceof Double || obj instanceof Boolean) {
            writer.append(String.valueOf(obj));
        } else {
            writer.append("'", obj.toString(), "'");
        }

        writer.append("}");
        return null;
    }

    private Void write(Module ex) {
        writer.append("{'type': 'module', 'body': [")
                .appendAll(ex.getExpressions(), ",")
                .append("]}");
        return null;
    }

    private Void write(Parenthesized ex) {
        writer.append("{'type': 'parens', 'body': [")
                .appendAll(ex.getExpressions(), ",")
                .append("]}");
        return null;
    }

    private Void write(Variable ex) {
        writer.append("{'type': 'variable', 'name': '")
                .append(ex.getName())
                .append("'}");
        return null;
    }


    public void visit(Expression ex) {
        Match(ex).of(
                Case(instanceOf(Assignment.class), this::write),
                Case(instanceOf(Call.class), this::write),
                Case(instanceOf(Export.class), this::write),
                Case(instanceOf(Import.class), this::write),
                Case(instanceOf(Lambda.class), this::write),
                Case(instanceOf(Literal.class), this::write),
                Case(instanceOf(Module.class), this::write),
                Case(instanceOf(Parenthesized.class), this::write),
                Case(instanceOf(Variable.class), this::write)
        );
    }
    
    
    
    
    
    
}
