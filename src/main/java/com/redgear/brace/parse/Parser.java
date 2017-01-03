package com.redgear.brace.parse;

import com.redgear.brace.ast.*;
import com.redgear.brace.lex.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by LordBlackHole on 2016-12-30.
 */
public class Parser {

    private static final Logger log = LoggerFactory.getLogger(Parser.class);
    private static final Set<String> unaryOps = new HashSet<>(Arrays.asList("!", "++", "--"));
    private final Iterator<Token> tokens;
    private final Deque<Token> backlog = new LinkedList<>();
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

        Module mod = new Module(start.getLocation(), modName.getValue());

        OperatorToken close = nextType(OperatorToken.class, ";");

        if(!";".equals(close.getValue())) {
            throw syntaxError(close, ";");
        }

        while (tokens.hasNext()) {
            Expression statement = readStatement();
            if(statement != null)
                mod.getExpressions().add(statement);
        }

        verifyImmutable(mod.getExpressions());

        return mod;
    }

    private RuntimeException syntaxError(Token token, String expected) {
        return syntaxError(token.getLocation(), expected, token.getValue());
    }

    private RuntimeException syntaxError(Location location, String expected, String found) {
        return new RuntimeException("Syntax error: expected '" + expected + "', found: '" + found + "' " + location.print());
    }

    private void backFill(Token token) {
        backlog.push(token);
    }

    private Token next() {
        if (backlog.isEmpty()) {
            if (tokens.hasNext()) {
                latest = tokens.next();
                return latest;
            } else {
                throw new RuntimeException("Unexpected end of file");
            }
        } else {
            return backlog.pop();
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
            } else if("import".equals(word)){
                return readImport();
            } else if("export".equals(word)){
                return readExport();
            } else {
                backFill(begin);
                return readExpression();
            }
        } else if(begin instanceof OperatorToken) {
            String op = begin.getValue();

            if(";}".contains(op)) {
                return null;
            } else {
                throw syntaxError(begin, "statement");
            }

        } else {
            return readExpression();
        }
    }

    private Expression readExpression() {

        Token begin = next();

        if(begin instanceof LiteralToken) {
            return readExpression(new Literal(begin.getLocation(), ((LiteralToken) begin).getRealValue()));
        } else if (begin instanceof WordToken) {
            String word = begin.getValue();

            if("true".equals(word)) {
                return readExpression(new Literal(begin.getLocation(), true));
            } else if("false".equals(word)) {
                return readExpression(new Literal(begin.getLocation(), false));
            }else if("_".equals(word)) {
                return readExpression(new Literal(begin.getLocation(), null));
            } else {
                return readExpression(new Variable(begin.getLocation(), word));
            }
        } else if(begin instanceof OperatorToken){
            String op = begin.getValue();

            if("(".equals(op)) {
                return readExpression(readExpression());
            } if("{".equals(op)){
                return readExpression(readFunction());
            } if(unaryOps.contains(op) || "-".equals(op)) {
                return readUnaryOp(readExpression(), op);
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


            if("});,".contains(op)) {
                return left;
            } else if("(".equals(op)){
                return readExpression(readApplication(left));
            } else if(unaryOps.contains(op)) {
                return readUnaryOp(left, op);
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


        return new Assignment(id.getLocation(), new Variable(id.getLocation(), id.getValue()), expression);
    }

    private Call readOperator(Expression left, String op) {
        Call call = new Call(left.getLocation(), LibraryModule.get(), new Variable(left.getLocation(),op));

        Expression right = readExpression();

        call.getArguments().add(left);
        call.getArguments().add(right);

        return call;
    }

    private Call readUnaryOp(Expression ex, String op) {
        return new Call(ex.getLocation(), LibraryModule.get(), new Variable(ex.getLocation(), op), ex);
    }

    private Call readApplication(Expression name) {
        return readApplication(ThisModule.get(), name);
    }


    private Call readApplication(ModuleRef mod, Expression name) {
        List<Expression> args = new ArrayList<>();

        do {
            args.add(readExpression());
        } while(",".equals(latest.getValue()));

        return new Call(name.getLocation(), mod, name, args);
    }

    private Func readFunction() {
        List<Variable> args = new ArrayList<>();

        boolean more = true;
        Token token;
        Location start = latest.getLocation();

        while(more) {
            token = next();

            if(token instanceof OperatorToken) {
                String op = token.getValue();
                if("=>".equals(op)) {
                    more = false;
                } else if(!",".equals(op)) {
                    throw syntaxError(token, "=>");
                }

            } else if(token instanceof WordToken) {
                String word = token.getValue();
                args.add(new Variable(token.getLocation(), word));
            } else {
                throw syntaxError(token, "arguments");
            }
        }


        List<Expression> statements = new ArrayList<>();

        while(!"}".equals(latest.getValue())) {
            Expression ex = readStatement();
            if(ex != null) {
                if(ex instanceof Import) {
                    throw syntaxError(ex.getLocation(), "statements", "import");
                }

                if(ex instanceof Export) {
                    throw syntaxError(ex.getLocation(), "statements", "export");
                }

                statements.add(ex);
            }
        }

        verifyImmutable(statements);

        return new Func(start, args, statements);
    }

    private Import readImport() {
        WordToken name = nextType(WordToken.class, "identifier");

        WordToken from = nextType(WordToken.class, "from");

        if(!"from".equals(from.getValue())) {
            throw syntaxError(from, "from");
        }

        LiteralToken module = nextType(LiteralToken.class, "module");

        if (!(module.getRealValue() instanceof String)) {
            throw syntaxError(module, "module");
        }

        return new Import(name.getLocation(), new ModuleRef(name.getValue()), module.getValue());
    }

    private Export readExport() {
        WordToken name = nextType(WordToken.class, "identifier");
        String value = name.getValue();

        OperatorToken assign = nextType(OperatorToken.class, "=");

        if(";".equals(assign.getValue())) {
            return new Export(name.getLocation(), value, new Variable(name.getLocation(), value));
        } else if("=".equals(assign.getValue())) {
            return new Export(name.getLocation(), value, readExpression());
        } else {
            throw syntaxError(assign, "=");
        }
    }

    private void verifyImmutable(List<Expression> statements) {
        List<Assignment> assigns = statements.stream()
                .filter(ex -> ex instanceof Assignment)
                .map(ex -> (Assignment) ex)
                .filter(ex -> !"_".equals(ex.getVar().getName()))
                .collect(Collectors.toList());

        Set<String> varNames = new HashSet<>();

        for (Assignment assign : assigns) {
            String name = assign.getVar().getName();

            if(varNames.contains(name)) {
                throw new RuntimeException("Reassignment to var '" + name + "' " + assign.getLocation().print());
            } else {
                varNames.add(name);
            }
        }
    }

}
