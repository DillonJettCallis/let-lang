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
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FileLoader implements Loader {
    private final Map<String, Module> loadedModules = new HashMap<>();
    private final Path base;

    public FileLoader(Path base) {
        this.base = base;
    }

    @Override
    public Module loadModule(String moduleName) {
        if (loadedModules.containsKey(moduleName)) {
            return loadedModules.get(moduleName);
        }

        var modulePath = base.resolve(moduleName.replace(".", "/") + ".let");

        if (!modulePath.toFile().exists()) {
            throw new RuntimeException("Could not find module with name: " + moduleName);
        }

        try {
            CharStream fileStream = CharStreams.fromPath(modulePath, Charset.forName("UTF-8"));

            LetLexer lexer = new LetLexer(fileStream);

            CommonTokenStream tokens = new CommonTokenStream(lexer);

            LetParser parser = new LetParser(tokens);

            LetParser.ModuleContext context = parser.module();

            AstBuilder builder = new AstBuilder();

            Module result = builder.build(context);
            loadedModules.put(moduleName, result);
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Failed to import module: " + modulePath, e);
        }
    }
}
