package com.redgear.let.antlr;

import com.redgear.let.AstPrinter;
import com.redgear.let.ast.Module;
import com.redgear.let.compile2js.Compiler2js;
import com.redgear.let.eval.Interpreter;
import com.redgear.let.eval.LibraryScope;
import com.redgear.let.lib.CoreLibrary;
import com.redgear.let.lib.ListLibrary;
import com.redgear.let.lib.MapLibrary;
import com.redgear.let.lib.StringLibrary;
import com.redgear.let.load.FileLoader;
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
        var loader = new FileLoader(Paths.get("src/test/resources/"));

        var libTypeScope = new LibraryTypeScope();
        var typeChecker = new TypeChecker(libTypeScope, loader);

        var libScope = new LibraryScope();
        var interpreter = new Interpreter(libScope, typeChecker);


        var coreLib = new CoreLibrary();
        coreLib.buildTypes(libTypeScope);
        coreLib.buildLibrary(interpreter, libScope);

        var listLib = new ListLibrary();
        typeChecker.loadLibModule(listLib);
        interpreter.loadLibModule(listLib);

        var mapLib = new MapLibrary();
        typeChecker.loadLibModule(mapLib);
        interpreter.loadLibModule(mapLib);

        var stringLib = new StringLibrary();
        typeChecker.loadLibModule(stringLib);
        interpreter.loadLibModule(stringLib);


        interpreter.run("BasicAssignmentTest");
    }

    @Test
    void dynamicInterpreterTest() {
        var loader = new FileLoader(Paths.get("src/test/resources/"));

        var libScope = new LibraryScope();
        var interpreter = new Interpreter(libScope, loader);

        var coreLib = new CoreLibrary();
        coreLib.buildLibrary(interpreter, libScope);
        interpreter.loadLibModule(new ListLibrary());
        interpreter.loadLibModule(new MapLibrary());
        interpreter.loadLibModule(new StringLibrary());

        interpreter.run("BasicAssignmentTest");
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
            var loader = new FileLoader(Paths.get("src/test/resources"));
            Module module = loader.loadModule("BasicAssignmentTest");

            var printer = new AstPrinter(buffered);

            printer.visit(module);
        }
    }

    @Test
    void basicTypeCheckerTest() {
        var loader = new FileLoader(Paths.get("src/test/resources/"));

        var libTypeScope = new LibraryTypeScope();
        var typeChecker = new TypeChecker(libTypeScope, loader);

        new CoreLibrary().buildTypes(libTypeScope);
        typeChecker.loadLibModule(new ListLibrary());
        typeChecker.loadLibModule(new MapLibrary());
        typeChecker.loadLibModule(new StringLibrary());

        typeChecker.loadModule("BasicAssignmentTest");
    }

}
