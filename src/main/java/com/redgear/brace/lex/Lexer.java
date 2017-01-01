package com.redgear.brace.lex;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Reader;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by LordBlackHole on 2017-01-01.
 */
public class Lexer {

    private final Reader reader;
    private static final String sticky = "+-*/=&|";
    private static final String ops = ".,;(){}[]<>" + sticky;
    private char latest = ' ';
    private int row = 1;
    private int column = 0;
    private boolean finished = false;

    public Lexer(Reader reader) {
        this.reader = reader;
    }


    public Stream<Token> tokenize() {

        return StreamSupport.stream(new Spliterators.AbstractSpliterator<Token>(Long.MAX_VALUE, Spliterator.ORDERED) {

            @Override
            public boolean tryAdvance(Consumer<? super Token> action) {

                eatWhitespace();

                if(finished) {
                    return false;
                } else if (latest == '"') {
                    action.accept(readStringValue(location()));
                } else if (Character.isDigit(latest)) {
                    action.accept(readNumberValue(location()));
                } else if (Character.isJavaIdentifierStart(latest)) {
                    action.accept(readWord(location()));
                } else if(ops.contains(String.valueOf(latest))) {
                    action.accept(readOperator(location()));
                } else {
                    throw new RuntimeException("Syntax error: " + location().print());
                }

                return true;
            }
        }, false);
    }


    @SuppressWarnings("Duplicates")
    private void eatChar() {
        if(finished)
            return;

        try {
            int next = reader.read();
            if(next == -1) {
                finished = true;
                return;
            }
            latest = (char) next;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(latest == '\n') {
            row++;
            column = 0;
        } else {
            column++;
        }
    }

    private void eatWhitespace() {
        while(!finished && Character.isWhitespace(latest)) {
            eatChar();
        }
    }

    private Location location() {
        return new Location(row, column);
    }


    private WordToken readWord(Location location) {
        StringBuilder builder = new StringBuilder();

        do {
            builder.append(latest);
            eatChar();
        } while (!finished && Character.isJavaIdentifierPart(latest));

        return new WordToken(location, builder.toString());
    }

    private StringToken readStringValue(Location location) {
        StringBuilder builder = new StringBuilder();

        eatChar();

        while (!finished && latest != '"') {
            builder.append(latest);
            eatChar();
        }

        eatChar();

        return new StringToken(location, builder.toString());
    }

    private LiteralToken readNumberValue(Location location) {
        StringBuilder builder = new StringBuilder();

        do {
            builder.append(latest);
            eatChar();

        } while(!finished && Character.isDigit(latest) || latest == '.');

        String value = builder.toString();

        int points = StringUtils.countMatches(value, ".");

        if(points == 0) {
            return new IntegerToken(location, Integer.valueOf(value));
        } else if(points == 1) {
            return new DoubleToken(location, Double.valueOf(value));
        } else {
            throw new NumberFormatException("Invalid number: '" + value + "' " + location.print());
        }
    }

    private OperatorToken readOperator(Location location) {
        if(sticky.contains(String.valueOf(latest))) {
            StringBuilder builder = new StringBuilder();

            do {
                builder.append(latest);
                eatChar();
            } while(!finished && sticky.contains(String.valueOf(latest)));

            return new OperatorToken(location, builder.toString());
        } else {
            String op = String.valueOf(latest);
            eatChar();
            return new OperatorToken(location, op);
        }

    }

}
