package com.redgear.let.lex;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by LordBlackHole on 2017-01-01.
 */
public class Lexer {

    private static final Logger log = LoggerFactory.getLogger(Lexer.class);
    private final Reader reader;
    private static final String sticky = "+-*/=&|<>!";
    private static final String ops = ".,;(){}[]" + sticky;
    private static final String wordStarts = "_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String wordAll = "0123456789." + wordStarts;
    private static final Set<String> keywords = new HashSet<>(Arrays.asList("let", "export", "import"));
    private final Deque<Character> braceBalence = new LinkedList<>();
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

                if(finished) {
                    return false;
                }

                eatWhitespace();

                if(finished) {

                    if(!braceBalence.isEmpty()) {
                        throw new RuntimeException("Unexpected end of file: Opened let: " + braceBalence.pop() + " is unclosed");
                    }

                    action.accept(new OperatorToken(location(), ";"));
                    return true;
                } else if (latest == '"') {
                    action.accept(readStringValue(location()));
                } else if (Character.isDigit(latest)) {
                    action.accept(readNumberValue(location()));
                } else if (contains(wordStarts, latest)) {
                    WordToken word = readWord(location());

                    if(keywords.contains(word.getValue())) {
                        action.accept(new OperatorToken(word.getLocation(), ";"));
                    }

                    action.accept(word);
                } else if(contains(ops, latest)) {
                    action.accept(readOperator(location()));
                } else {
                    throw new RuntimeException("Syntax error: " + location().print());
                }

                return true;
            }
        }, false);
    }

    private void eatChar() {
        if(finished)
            return;

        try {
            boolean comment = false;
            do {
                int next = reader.read();
                if (next == -1) {
                    finished = true;
                    return;
                }
                latest = (char) next;

                if(latest == '#') {
                    comment = true;
                }

                if(comment && latest == '\n') {
                    comment = false;
                }

            } while (comment);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(latest == '\n') {
            row++;
            column = 0;
        } else if(latest == '\t'){ //Tabs
            column += 4;
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
        } while (!finished && contains(wordAll, latest));

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
        if(contains(sticky, latest)) {
            StringBuilder builder = new StringBuilder();

            do {
                builder.append(latest);
                eatChar();
            } while(!finished && contains(sticky, latest));

            return new OperatorToken(location, builder.toString());
        } else {
            char op = latest;

            if(contains("({[", op)) {
                braceBalence.push(op);
            } else if (contains(")]}", op)) {

                if(braceBalence.isEmpty()) {
                    throw new RuntimeException("Closing " + op + " without an opening " + location.print());
                }

                char open = braceBalence.pop();

                if ((!('(' == open) || !(')' == op)) && (!('{' == open) || !('}' == op)) && (!('[' == open) || !(']' == op))) {
                    throw new RuntimeException("Unbalanced braces: Opened: " + open + " closed: " + op + " " + location.print());
                }

            }

            eatChar();
            return new OperatorToken(location, String.valueOf(op));
        }

    }

    private boolean contains(String s, char i) {
        return s.indexOf(i) != -1;
    }

}
