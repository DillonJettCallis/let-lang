package com.redgear.let.eval;

import com.redgear.let.antlr.LetLexer;
import com.redgear.let.antlr.LetParser;
import com.redgear.let.ast.AstBuilder;
import com.redgear.let.ast.Module;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LordBlackHole on 2017-01-07.
 */
public class Interpreter {

    private final LibraryScope libraryScope;
    private final Path mainModule;
    private final Map<String, ModuleScope> modules = new HashMap<>();

    public Interpreter(String modulePath) {
        this.mainModule = Paths.get(modulePath).toAbsolutePath();
        this.libraryScope = new LibraryScope();
        new CoreLibrary(this).buildLibrary(libraryScope);
    }

    private String resolveModule(String relativeModule) {
        return mainModule.resolveSibling(relativeModule).toAbsolutePath().toString();
    }

    public void run() {
        loadModuleReal(mainModule.toString());
    }

    public ModuleScope loadModule(String fileName) {
        String resolved = resolveModule(fileName);

        return modules.computeIfAbsent(resolved, this::loadModuleReal);
    }

    private ModuleScope loadModuleReal(String filePath) {
        try {
            ANTLRFileStream fileStream = new ANTLRFileStream(filePath, "UTF-8");

            LetLexer lexer = new LetLexer(fileStream);

            CommonTokenStream tokens = new CommonTokenStream(lexer);

            LetParser parser = new LetParser(tokens);

            LetParser.ModuleContext context = parser.module();

            AstBuilder builder = new AstBuilder(this);

            Module module = builder.build(context);

            return (ModuleScope) module.eval(new LocalScope(libraryScope));
        } catch (IOException e) {
            throw new RuntimeException("Failed to import module: " + filePath, e);
        }
    }
}
