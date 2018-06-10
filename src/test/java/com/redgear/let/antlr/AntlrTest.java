package com.redgear.let.antlr;

import com.redgear.let.AstPrinter;
import com.redgear.let.ast.Module;
import com.redgear.let.compile2js.Compiler2js;
import com.redgear.let.eval.Interpreter;
import com.redgear.let.load.Loader;
import com.redgear.let.types.LibraryTypeScope;
import com.redgear.let.types.TypeChecker;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

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
    void basicAstTest() throws IOException {
        var path = new File("build/test/ast/withoutParens.json");

        path.getParentFile().mkdirs();

        try (var writer = new FileWriter(path.toString());
             var buffered = new BufferedWriter(writer)) {
            var loader = new Loader(Paths.get("src/test/resources"));
            Module module = loader.loadModule("BasicAssignmentTest");

            var printer = new AstPrinter(buffered);

            printer.visit(module);
        }
    }

    @Test
    void basicTypeCheckerTest() {
        var loader = new Loader(Paths.get("src/test/resources"));
        Module module = loader.loadModule("BasicAssignmentTest");

        var libraryScope = new LibraryTypeScope();
        var typeChecker = new TypeChecker(libraryScope, loader);
        var types = typeChecker.visit(module);

        log.debug("TypeChecker output: {}", types);
    }

}
