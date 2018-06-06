package com.redgear.let.compile2js;

import com.redgear.let.ast.Expression;

import java.io.IOException;
import java.io.Writer;

public class SafeWriter {

    private final Writer writer;
    private final Compiler compiler;

    public SafeWriter(Writer writer, Compiler compiler) {
        this.writer = writer;
        this.compiler = compiler;
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
        compiler.compile(this, ex);
        return this;
    }
}
