package com.redgear.let.compile2js;

import com.redgear.let.antlr.LetLexer;
import com.redgear.let.antlr.LetParser;
import com.redgear.let.ast.AstBuilder;
import com.redgear.let.ast.Module;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Compiler2js {

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
                new Compile2JsVisitor(writer).visit(module);
            }


        } catch (IOException e) {
            throw new RuntimeException("Failed to compile module: " + mainModule, e);
        }
    }


}
