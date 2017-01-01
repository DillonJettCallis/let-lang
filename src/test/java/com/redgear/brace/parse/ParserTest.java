package com.redgear.brace.parse;

import com.redgear.brace.ast.Module;
import com.redgear.brace.eval.Eval;
import com.redgear.brace.format.Formatter;
import com.redgear.brace.lex.Lexer;
import com.redgear.brace.visual.Visualizer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by LordBlackHole on 2016-12-30.
 */
public class ParserTest {

    private static final Logger log = LoggerFactory.getLogger(ParserTest.class);

    @Test
    public void basicAssignmentTest() throws IOException, InterruptedException {

        Module mod = withReader(new File("src/test/resources/basicAssignmentTest.txt"), reader -> new Parser(new Lexer(reader).tokenize().iterator()).readModule());


//        format(mod);
        visualize(mod);
        evaluate(mod);

    }


    @FunctionalInterface
    private interface CheckedFunc<In, Out> {

        Out apply(In reader) throws IOException;

    }


    private <T> T withReader(File file, CheckedFunc<Reader, T> func) {

        try(FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            return func.apply(bufferedReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void format(Module mod) throws IOException {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(System.out))) {

            log.info("Pretty printed: ");

            Formatter formatter = new Formatter(writer);

            formatter.walk(mod);

            writer.flush();
        }
    }

    private void visualize(Module mod) throws IOException {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(System.out))) {
            log.info("Visualized: ");

            Visualizer visualizer = new Visualizer(writer);

            visualizer.walk(mod);

            writer.flush();
        }
    }

    private void evaluate(Module mod) {
        log.info("Evaluated: ");

        Eval eval = new Eval();

        eval.walk(mod);
    }

}
