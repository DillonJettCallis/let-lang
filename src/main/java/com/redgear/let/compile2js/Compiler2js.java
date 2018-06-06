package com.redgear.let.compile2js;

import com.redgear.let.antlr.LetLexer;
import com.redgear.let.antlr.LetParser;
import com.redgear.let.ast.*;
import com.redgear.let.ast.Module;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.Predicates.instanceOf;

public class Compiler2js implements Compiler {

    private final Path mainModule;
    private final Path outputPath;

    public Compiler2js(String modulePath, String outputPath) {
        this.mainModule = Paths.get(modulePath).toAbsolutePath();
        this.outputPath = Paths.get(outputPath).toAbsolutePath();
    }

    public void compile() {

        try {
            CharStream fileStream = CharStreams.fromPath(mainModule, Charset.forName("UTF-8"));

            LetLexer lexer = new LetLexer(fileStream);

            CommonTokenStream tokens = new CommonTokenStream(lexer);

            LetParser parser = new LetParser(tokens);

            LetParser.ModuleContext context = parser.module();

            AstBuilder builder = new AstBuilder();

            Module module = builder.build(context);

            File outFile = outputPath.toFile();
            outFile.delete();
            outFile.getParentFile().mkdirs();

            try (Writer writer = new BufferedWriter(new FileWriter(outFile))) {
                compile(new SafeWriter(writer, this), module);
            }


        } catch (IOException e) {
            throw new RuntimeException("Failed to compile module: " + mainModule, e);
        }
    }

    private Void compile(SafeWriter writer, Assignment ex) {
        writer.append("const ", ex.getVar().getName(), " = ")
                .append(ex.getExp())
                .append(";");
        return null;
    }

    private Void compile(SafeWriter writer, Call ex) {
        if (ex.getName().equals("if")) {
            compileIf(writer, ex);
        } else if (ex.getName().equals(".")) {
            compileAccess(writer, ex);
        } else if (ex.getName().matches("^\\W+$")) {
            compileOp(writer, ex);
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

    private Void compileIf(SafeWriter writer, Call ex) {
        writer.append("((")
                .append(ex.getArguments().get(0))
                .append(")?(")
                .append(ex.getArguments().get(1))
                .append(")")
                .append(":(")
                .append(ex.getArguments().getOrElse(new Variable(null, "_")))
                .append("))");

        return null;
    }

    private Void compileAccess(SafeWriter writer, Call ex) {
        writer.append("((")
                .append(ex.getArguments().get(0))
                .append(")[")
                .append(ex.getArguments().get(1))
                .append("])");

        return null;
    }

    private Void compileOp(SafeWriter writer, Call ex) {
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

    private Void compile(SafeWriter writer, Export ex) {
        writer.append("export const ", ex.getName(), " = ")
                .append(ex.getExpression())
                .append(";");
        return null;
    }

    private Void compile(SafeWriter writer, Import ex) {
        writer.append("import * as ", ex.getModuleName(), " from '", ex.getId(), "';");
        return null;
    }

    private Void compile(SafeWriter writer, Lambda ex) {
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

    private Void compile(SafeWriter writer, Literal ex) {
        Match(ex.getValue()).of(
                Case(instanceOf(String.class), value -> writer.append("'", value, "'")),
                Case(instanceOf(Integer.class), value -> writer.append(String.valueOf(value))),
                Case(instanceOf(Double.class), value -> writer.append(String.valueOf(value)))
        );
        return null;
    }

    private Void compile(SafeWriter writer, Module ex) {
        for (Expression next : ex.getExpressions()) {
            writer.append(next)
                    .append(";");
        }
        return null;
    }

    private Void compile(SafeWriter writer, Parenthesized ex) {
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

    private Void compile(SafeWriter writer, Variable ex) {
        if (ex.getName().equals("_")) {
            writer.append("undefined");
        } else {
            writer.append(ex.getName());
        }
        return null;
    }

    public void compile(SafeWriter writer, Expression expression) {
        Match(expression).of(
                Case(instanceOf(Assignment.class), ex -> compile(writer, ex)),
                Case(instanceOf(Call.class), ex -> compile(writer, ex)),
                Case(instanceOf(Export.class), ex -> compile(writer, ex)),
                Case(instanceOf(Import.class), ex -> compile(writer, ex)),
                Case(instanceOf(Lambda.class), ex -> compile(writer, ex)),
                Case(instanceOf(Literal.class), ex -> compile(writer, ex)),
                Case(instanceOf(Module.class), ex -> compile(writer, ex)),
                Case(instanceOf(Parenthesized.class), ex -> compile(writer, ex)),
                Case(instanceOf(Variable.class), ex -> compile(writer, ex))
        );
    }
}
