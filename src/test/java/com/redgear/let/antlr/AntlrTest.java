package com.redgear.let.antlr;

import com.redgear.let.compile2js.Compiler2js;
import com.redgear.let.eval.Interpreter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AntlrTest {

    private static final Logger log = LoggerFactory.getLogger(AntlrTest.class);

    @Test
    public void basicAntlrTest() {
        new Interpreter("src/test/resources/basicAssignmentTest.let").run();
    }

    @Test
    public void basicJsTest() {
        new Compiler2js("src/test/resources/basicAssignmentTest.let", "build/test/js/basicAssignmentTest.js").compile();
    }

}
