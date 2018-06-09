package com.redgear.let.tools;

import com.redgear.let.ast.AstVisitor;
import com.redgear.let.ast.Expression;
import javaslang.collection.List;

import java.io.IOException;
import java.io.Writer;

public class SafeWriter {

    private final Writer writer;
    private final AstVisitor astVisitor;

    public SafeWriter(Writer writer, AstVisitor astVisitor) {
        this.writer = writer;
        this.astVisitor = astVisitor;
    }

    public SafeWriter append(String next) {
        try {
            writer.append(next);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }


    public SafeWriter append(String... all) {
        for (String next : all) {
            append(next);
        }
        return this;
    }

    public SafeWriter append(Expression ex) {
        astVisitor.visit(ex);
        return this;
    }

    public SafeWriter appendAll(List<? extends Expression> list, String deliminator) {
        var args = list.toArray();

        for (int i = 0; i < args.length(); i++) {
            append(args.get(i));
            if (i != args.length() - 1) {
                append(deliminator);
            }
        }
        return this;
    }
}
