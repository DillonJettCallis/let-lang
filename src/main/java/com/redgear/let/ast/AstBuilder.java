package com.redgear.let.ast;

import com.redgear.let.antlr.LetParser.*;
import com.redgear.let.eval.Interpreter;
import javaslang.collection.HashSet;
import javaslang.collection.List;
import javaslang.collection.Set;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.Predicates.instanceOf;

/**
 * Created by LordBlackHole on 2017-01-07.
 */
public class AstBuilder {

    private static final Logger log = LoggerFactory.getLogger(AstBuilder.class);
    private static final Set<String> keywords = HashSet.of("true", "false");
    private final Interpreter interpreter;

    public AstBuilder(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public Module build(ModuleContext module) {
        List<Expression> expressions = List.ofAll(module.statement()).map(this::build);

        return new Module(new Location(module.getStart()),expressions);
    }

    private Expression build(ExpressionStatementContext expression) {
        return build(expression.expression());
    }

    private Expression build(ImportStatementContext context) {
        PortInContext portIn = context.portIn();

        String id = portIn.ModuleIdentifier().getText();

        String body = portIn.StringLiteral().getText();

        String value = body.substring(1, body.length() - 1);

        return new Import(new Location(context.getStart()), interpreter, id, value);
    }

    private Expression build(ExportStatementContext portOut) {
        PortOutContext context = portOut.portOut();

        ExpressionContext expression = context.expression();

        if(expression == null) {
            Variable var = makeVariable(context.LocalIdentifier());

            return new Export(new Location(portOut.getStart()), var.getName(), var);
        } else {
            String id = context.LocalIdentifier().getText();

            Expression ex = build(expression);

            return new Export(new Location(portOut.getStart()), id, ex);
        }
    }

    private Expression build(AssignmentExpressionContext context) {
        Variable var = makeVariable(context.LocalIdentifier());

        Expression ex = build(context.expression());

        if("_".equals(var.getName())) {
            return ex;
        } else if(keywords.contains(var.getName())) {
            throw new RuntimeException("Can't assign to: " + var.getName() + " " + var.getLocation().print());
        } else {
            return new Assignment(new Location(context.getStart()), var, ex);
        }
    }

    private Lambda build(FunctionExpressionContext context) {
        List<Variable> args = List.ofAll(context.LocalIdentifier()).map(this::makeVariable);

        List<Expression> expressions = List.ofAll(context.expression()).map(this::build);

        return new Lambda(new Location(context.getStart()), args, expressions);
    }

    private Call build(CallExpressionContext context) {
        List<Expression> args = List.ofAll(context.args).map(this::build);

        Expression method = build(context.method);

        return new Call(new Location(context.start), method, args);
    }

    private Call build(ModuleAccessExpressionContext context) {
        Expression ex = build(context.expression());
        Literal var = new Literal(new Location(context.LocalIdentifier().getSymbol()), context.LocalIdentifier().getText());

        return new Call(new Location(context.getStart()), new Variable(var.getLocation(), "."), List.of(ex, var));
    }

    private Call build(UnaryOpExpressionContext context) {
        Variable var = new Variable(new Location(context.op), context.op.getText());

        Expression ex = build(context.expression());

        return new Call(new Location(context.getStart()), var, List.of(ex));
    }

    private Call build(BinaryOpExpressionContext context) {
        Variable var = new Variable(new Location(context.op), context.op.getText());

        Expression left  = build(context.expression(0));
        Expression right = build(context.expression(1));

        return new Call(new Location(context.op), var, List.of(left, right));
    }

    private Parenthesized build(ParenthesizedExpressionContext context) {
        List<Expression> expressions = List.ofAll(context.expression()).map(this::build);

        return new Parenthesized(new Location(context.getStart()), expressions);
    }

    private Variable build(LocalIdentifierExpressionContext context) {
        return makeVariable(context.LocalIdentifier());
    }

    private Variable build(ModuleIdentifierExpressionContext context) {
        return makeVariable(context.ModuleIdentifier());
    }

    private Literal build(IntLiteralExpressionContext context) {
        return new Literal(new Location(context.getStart()), Integer.parseInt(context.IntLiteral().getText()));
    }

    private Literal build(FloatLiteralExpressionContext context) {
        return new Literal(new Location(context.getStart()), Double.parseDouble(context.FloatLiteral().getText()));
    }

    private Literal build(StringLiteralExpressionContext context) {
        String body = context.StringLiteral().getText();

        String value = body.substring(1, body.length() - 1);

        Location location = new Location(context.getStart());

        return new Literal(location, value);
    }

    private Call build(MapLiteralExpressionContext context) {
        Location location = new Location(context.getStart());
        List<Expression> expressions = List.ofAll(context.expression()).map(this::build);

        return new Call(location, buildQualifiedFunc(location, "Map", "build"), expressions);
    }

    private Call build(ListLiteralExpressionContext context) {
        Location location = new Location(context.getStart());
        List<Expression> expressions = List.ofAll(context.expression()).map(this::build);

        return new Call(location, buildQualifiedFunc(location, "List", "build"), expressions);
    }

    private Expression build(StatementContext context) {


        return Match(context).of(
                Case(instanceOf(ImportStatementContext.class), this::build),
                Case(instanceOf(ExportStatementContext.class), this::build),
                Case(instanceOf(ExpressionStatementContext.class), this::build)
        );
    }

    private Expression build(ExpressionContext context) {

        return Match(context).of(
                Case(instanceOf(AssignmentExpressionContext.class), this::build),
                Case(instanceOf(FunctionExpressionContext.class), this::build),
                Case(instanceOf(CallExpressionContext.class), this::build),
                Case(instanceOf(ModuleAccessExpressionContext.class), this::build),
                Case(instanceOf(UnaryOpExpressionContext.class), this::build),
                Case(instanceOf(BinaryOpExpressionContext.class), this::build),
                Case(instanceOf(ParenthesizedExpressionContext.class), this::build),
                Case(instanceOf(LocalIdentifierExpressionContext.class), this::build),
                Case(instanceOf(ModuleIdentifierExpressionContext.class), this::build),
                Case(instanceOf(IntLiteralExpressionContext.class), this::build),
                Case(instanceOf(FloatLiteralExpressionContext.class), this::build),
                Case(instanceOf(StringLiteralExpressionContext.class), this::build),
                Case(instanceOf(MapLiteralExpressionContext.class), this::build),
                Case(instanceOf(ListLiteralExpressionContext.class), this::build)
        );
    }

    private Variable makeVariable(TerminalNode node) {
        return new Variable(new Location(node.getSymbol()), node.getText());
    }

    private Call buildQualifiedFunc(Location location, String module, String function) {
        Variable modVar = new Variable(location, module);
        Literal funVar = new Literal(location, function);

        Variable dotAccess = new Variable(location, ".");

        return new Call(location, dotAccess, List.of(modVar, funVar));
    }
}
