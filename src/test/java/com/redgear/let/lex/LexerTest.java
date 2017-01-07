package com.redgear.let.lex;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by LordBlackHole on 2017-01-01.
 */
public class LexerTest {

    private static final Logger log = LoggerFactory.getLogger(LexerTest.class);

    @Test
    public void basicTest() throws IOException {

        withReader(new File("src/test/resources/basicAssignmentTest.txt"), reader -> {

            new Lexer(reader).tokenize().map(Token::getValue).forEach(log::info);

            return "";

        });
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


}
