package com.redgear.let;

import com.redgear.let.ast.*;
import com.redgear.let.ast.Module;
import com.redgear.let.tools.SafeWriter;

import java.io.Writer;

public class AstPrinter implements AstVisitor {
    
    private final SafeWriter writer;

    public AstPrinter(Writer writer) {
        this.writer = new SafeWriter(writer, this);
    }
    
    
    public Void visit(Assignment ex) {
        writer.append("{'type': 'assign', 'var': ").append(ex.getVar()).append(", 'ex': ").append(ex.getBody()).append("}");
        return null;
    }

    public Void visit(Call ex) {
        writer.append("{'type': 'call', 'method': ")
                .append(ex.getMethod())
                .append(", 'args': [")
                .appendAll(ex.getArguments(), ",")
                .append("] }");
        return null;
    }

    public Void visit(Export ex) {
        writer.append("{'type': 'export', 'name': '").append(ex.getName()).append("', 'value': ").append(ex.getExpression()).append("}");
        return null;
    }

    public Void visit(Import ex) {
        writer.append("{'type': 'import', 'name': '").append(ex.getAlias()).append("', 'path': '").append(ex.getPath()).append("'}");
        return null;
    }

    public Void visit(Lambda ex) {
        writer.append("{'type': 'lambda', 'vars': [")
                .appendAll(ex.getVariables(), ",")
                .append("], 'body': [")
                .appendAll(ex.getStatements(), ",")
                .append("]}");
        return null;
    }

    public Void visit(Literal ex) {
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

    public Void visit(Module ex) {
        writer.append("{'type': 'module', 'body': [")
                .appendAll(ex.getExpressions(), ",")
                .append("]}");
        return null;
    }

    public Void visit(Parenthesized ex) {
        writer.append("{'type': 'parens', 'body': [")
                .appendAll(ex.getExpressions(), ",")
                .append("]}");
        return null;
    }

    public Void visit(Variable ex) {
        writer.append("{'type': 'variable', 'name': '")
                .append(ex.getName())
                .append("'}");
        return null;
    }
}
