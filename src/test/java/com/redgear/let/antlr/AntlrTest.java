package com.redgear.let.antlr;

import com.redgear.let.ast.AstBuilder;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by LordBlackHole on 2017-01-07.
 */
public class AntlrTest {

    private static final Logger log = LoggerFactory.getLogger(AntlrTest.class);

    @Test
    public void basicAntlrTest() throws IOException {

        ANTLRFileStream fileStream = new ANTLRFileStream("src/test/resources/basicAssignmentTest.let", "UTF-8");

        LetLexer lexer = new LetLexer(fileStream);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        LetParser parser = new LetParser(tokens);

        LetParser.ModuleContext module = parser.module();

        AstBuilder eval = new AstBuilder();

        eval.build(module);
    }



}
