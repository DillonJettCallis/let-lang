package com.redgear.let.antlr;

import com.redgear.let.eval.Interpreter;
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

        new Interpreter("src/test/resources/basicAssignmentTest.let").run();

    }



}
