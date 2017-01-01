package com.redgear.brace.walk;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by LordBlackHole on 2016-12-30.
 */
public abstract class PrintingWalker implements Walker {

    protected final Writer writer;
    protected int indent = 0;

    public PrintingWalker(Writer writer) {
        this.writer = writer;
    }

    protected String printIndent() {
        if(indent == 0) {
            return "";
        } else {
            return StringUtils.leftPad("", indent * 2, "  ");
        }
    }

    protected void print(String value) {
        try {
            writer.write(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void print(String... values) {
        try {
            for(String value : values)
                writer.write(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
