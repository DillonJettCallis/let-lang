package com.redgear.let.antlr;

import com.redgear.let.AstPrinter;
import com.redgear.let.ast.AstBuilder;
import com.redgear.let.ast.Module;
import com.redgear.let.compile2js.Compiler2js;
import com.redgear.let.eval.Interpreter;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;

class AntlrTest {

    private static final Logger log = LoggerFactory.getLogger(AntlrTest.class);

    @Test
    void basicInterpreterTest() {
        new Interpreter("src/test/resources/").run("BasicAssignmentTest");
    }

    @Test
    void basicJsTest() {
        new Compiler2js("src/test/resources/BasicAssignmentTest.let", "build/test/js/basicAssignmentTest.js").compile();
    }

    @Test
    void basicAntlrTest() throws IOException {
        var path = new File("build/test/ast/withoutParens.json");

        path.getParentFile().mkdirs();

        try (var writer = new FileWriter(path.toString());
             var buffered = new BufferedWriter(writer)) {
            CharStream fileStream = CharStreams.fromFileName("src/test/resources/BasicAssignmentTest.let", Charset.forName("UTF-8"));

            LetLexer lexer = new LetLexer(fileStream);

            CommonTokenStream tokens = new CommonTokenStream(lexer);

            LetParser parser = new LetParser(tokens);

            LetParser.ModuleContext context = parser.module();

            AstBuilder builder = new AstBuilder();

            Module module = builder.build(context);

            var printer = new AstPrinter(buffered);

            printer.visit(module);
        }
    }

}
