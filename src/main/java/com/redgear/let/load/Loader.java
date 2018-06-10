package com.redgear.let.load;

import com.redgear.let.antlr.LetLexer;
import com.redgear.let.antlr.LetParser;
import com.redgear.let.ast.AstBuilder;
import com.redgear.let.ast.Module;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.nio.charset.Charset;

public class Loader {

    public Module loadModule(String filePath) {
        try {
            CharStream fileStream = CharStreams.fromFileName(filePath, Charset.forName("UTF-8"));

            LetLexer lexer = new LetLexer(fileStream);

            CommonTokenStream tokens = new CommonTokenStream(lexer);

            LetParser parser = new LetParser(tokens);

            LetParser.ModuleContext context = parser.module();

            AstBuilder builder = new AstBuilder();

            return builder.build(context);
        } catch (IOException e) {
            throw new RuntimeException("Failed to import module: " + filePath, e);
        }
    }
}
