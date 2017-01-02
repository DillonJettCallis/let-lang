package com.redgear.brace.parse;

import com.redgear.brace.ast.*;
import com.redgear.brace.lex.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by LordBlackHole on 2016-12-30.
 */
public class Parser {

    private static final Logger log = LoggerFactory.getLogger(Parser.class);
    private final Iterator<Token> tokens;
    private Token latest;

    public Parser(Iterator<Token> tokens) {
        this.tokens = tokens;
    }


    public Module readModule() {
        String cause = "module declaration";

        WordToken start = nextType(WordToken.class, cause);

        if(!"module".equals(start.getValue())) {
            throw syntaxError(start, cause);
        }

        WordToken modName = nextType(WordToken.class, "module name");

        Module mod = new Module(modName.getValue());

        OperatorToken close = nextType(OperatorToken.class, ";");

        if(!";".equals(close.getValue())) {
            throw syntaxError(close, ";");
        }

        while (tokens.hasNext()) {
            Expression statement = readStatement();
            if(statement != null)
                mod.getExpressions().add(statement);
        }

        return mod;
    }

    private RuntimeException syntaxError(Token token, String expected) {
        return syntaxError(token.getLocation(), expected, token.getValue());
    }

    private RuntimeException syntaxError(Location location, String expected, String found) {
        return new RuntimeException("Syntax error: expected '" + expected + "', found: '" + found + "' " + location.print());
    }

    private Token next() {
        if (tokens.hasNext()) {
            latest = tokens.next();
            return latest;
        } else {
            throw new RuntimeException("Unexpected end of file");
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Token> T nextType(Class<T> type, String expected) {
        Token next = next();

        if(type.isInstance(next)) {
            return (T) next;
        } else {
            throw syntaxError(next, expected);
        }
    }

    private Expression readStatement() {

        Token begin = next();

        if (begin instanceof WordToken) {
            String word = begin.getValue();

            if("let".equals(word)) {
                return readAssignment();
            } else {
                throw syntaxError(begin, "statement");
            }
        } else if(begin instanceof OperatorToken) {
            String op = begin.getValue();

            if(";".equals(op)) {
                return null;
            } else {
                throw syntaxError(begin, "statement");
            }

        } else {
            throw syntaxError(begin, "statement");
        }
    }

    private Expression readExpression() {

        Token begin = next();

        if(begin instanceof LiteralToken) {
            return readExpression(new Literal(((LiteralToken) begin).getRealValue()));
        } else if (begin instanceof WordToken) {
            String word = begin.getValue();

            if("true".equals(word)) {
                return readExpression(new Literal(true));
            } else if("false".equals(word)) {
                return readExpression(new Literal(false));
            }else if("_".equals(word)) {
                return readExpression(new Literal(null));
            } else {
                return readExpression(new Variable(word));
            }
        } else if(begin instanceof OperatorToken){
            String op = begin.getValue();

            if("(".equals(op)) {
                return readExpression(readExpression());
            } else {
                throw syntaxError(begin, "expression");
            }

        } else {
            throw syntaxError(begin, "expression");
        }
    }

    private Expression readExpression(Expression left) {

        Token begin = next();

        if(begin instanceof OperatorToken) {
            String op = begin.getValue();


            if(");,".contains(op)) {
                return left;
            } else if("(".equals(op)){
                return readApplication(left);
            } else {
                return readOperator(left, op);
            }

        } else {
            throw syntaxError(begin, "expression");
        }
    }

    private Assignment readAssignment() {

        WordToken id = nextType(WordToken.class, "identifier");

        OperatorToken assign = nextType(OperatorToken.class, "=");

        if(!"=".equals(assign.getValue())) {
            throw syntaxError(assign.getLocation(), "=", assign.getValue());
        }

        Expression expression = readExpression();


        return new Assignment(new Variable(id.getValue()), expression);
    }

    private Call readOperator(Expression left, String op) {
        Expression right = readExpression();

        Call call = new Call(LibraryModule.get(), new Variable(op));

        call.getArguments().add(left);
        call.getArguments().add(right);

        return call;
    }

    private Call readApplication(Expression name) {
        return readApplication(ThisModule.get(), name);
    }


    private Call readApplication(ModuleRef mod, Expression name) {
        List<Expression> args = new ArrayList<>();

        do {
            args.add(readExpression());
        } while(",".equals(latest.getValue()));

        return new Call(mod, name, args);
    }

}
