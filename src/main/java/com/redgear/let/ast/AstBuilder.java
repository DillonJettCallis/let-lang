package com.redgear.let.ast;

import com.redgear.let.antlr.LetParser.*;
import com.redgear.let.types.NamedTypeToken;
import javaslang.collection.*;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.Predicates.instanceOf;

public class AstBuilder {

    private static final Logger log = LoggerFactory.getLogger(AstBuilder.class);
    private static final Set<String> keywords = HashSet.of("true", "false");
    private static final Map<String, String> listOps = HashMap.of(
            "|", "map",
            "|/", "flatMap",
            "|?", "filter",
            "|!", "forEach",
            "|&", "fold");

    public AstBuilder() {

    }

    public Module build(ModuleContext module) {
        List<Expression> expressions = List.ofAll(module.statement()).map(this::build);

        return new Module(new Location(module.getStart()),expressions);
    }

    private Expression build(ImportStatementContext context) {
        var portIn = context.portIn();

        var tokens = List.ofAll(portIn.path).map(Token::getText);
        var path = tokens.mkString(".");

        var alias = portIn.alias == null ? tokens.last() : portIn.alias.getText();

        return new Import(new Location(context.getStart()), null, path, alias);
    }

    private Expression build(AssignmentExpressionContext context) {
        Variable var = makeVariable(context.LocalIdentifier());

        Expression ex = build(context.expression());

        if("_".equals(var.getName())) {
            return ex;
        } else if(keywords.contains(var.getName())) {
            throw new RuntimeException("Can't assign to: " + var.getName() + " " + var.getLocation().print());
        } else {
            return new Assignment(new Location(context.getStart()), null, var, ex);
        }
    }

    private Lambda build(FunctionExpressionContext context) {
        List<Variable> args = List.ofAll(context.LocalIdentifier()).map(this::makeVariable);

        List<Expression> expressions = List.ofAll(context.expression()).map(this::build);

        return new Lambda(new Location(context.getStart()), null, args, expressions);
    }

    private Expression build(FunctionStatementContext context) {
        var exported = context.exported != null;

        var types = List.ofAll(context.argTypes).map(type -> new NamedTypeToken(type.getText()));
        var resultType = new NamedTypeToken(context.resultType.getText());
        var ids = List.ofAll(context.LocalIdentifier());

        var id = makeVariable(ids.head());

        var args = ids.tail().map(this::makeVariable).zip(types).map(pair -> pair._1.setTypeToken(pair._2));

        var expressions = List.ofAll(context.expression()).map(this::build);

        var assignment = new Assignment(new Location(context.getStart()), null, id, new Lambda(new Location(context.getStart()), resultType, args, expressions));

        if (exported) {
            return new Export(new Location(context.getStart()), null, id.getName(), assignment);
        } else {
            return assignment;
        }
    }

    private Call build(CallExpressionContext context) {
        List<Expression> args = List.ofAll(context.args).map(this::build);

        Expression method = build(context.method);

        return new Call(new Location(context.start), null, method, args);
    }

    private Call build(ModuleAccessExpressionContext context) {
        Expression ex = build(context.expression());
        Literal var = new Literal(new Location(context.LocalIdentifier().getSymbol()), context.LocalIdentifier().getText());

        return new Call(new Location(context.getStart()), null, new Variable(var.getLocation(), null, "."), List.of(ex, var));
    }

    private Call build(UnaryOpExpressionContext context) {
        Variable var = new Variable(new Location(context.op), null, context.op.getText());

        Expression ex = build(context.expression());

        return new Call(new Location(context.getStart()), null, var, List.of(ex));
    }

    private Call build(BinaryOpExpressionContext context) {
        Location opLocation = new Location(context.op);
        String op = context.op.getText();

        Expression opExpression = listOps.containsKey(op)
                ? buildQualifiedFunc(opLocation, "List", listOps.get(op).get())
                : new Variable(opLocation, null, op);

        Expression left  = build(context.expression(0));
        Expression right = build(context.expression(1));

        return new Call(opLocation, null, opExpression, List.of(left, right));
    }

    private Parenthesized build(ParenthesizedExpressionContext context) {
        List<Expression> expressions = List.ofAll(context.expression()).map(this::build);

        return new Parenthesized(new Location(context.getStart()), null, expressions);
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

        return new Call(location, null, new Variable(location, null, "$buildMap"), expressions);
    }

    private Call build(ListLiteralExpressionContext context) {
        Location location = new Location(context.getStart());
        List<Expression> expressions = List.ofAll(context.expression()).map(this::build);

        return new Call(location, null, new Variable(location, null, "$buildList"), expressions);
    }

    private Call build(IfExpressionContext context) {
        Location location = new Location(context.getStart());
        Expression condition = build(context.condition);
        List<Expression> thenExs = List.ofAll(context.thenExpressions).map(this::build);
        List<Expression> elseExs = List.ofAll(context.elseExpressions).map(this::build);

        return new Call(location, null, new Variable(location, null, "if"), List.of(condition, new Parenthesized(new Location(context.getStart()), null, thenExs), new Parenthesized(new Location(context.getStart()), null, elseExs)));
    }

    private Call build(ForExpressionContext context) {
        Location location = new Location(context.getStart());
        Variable local = makeVariable(context.LocalIdentifier());
        Expression collection = build(context.collection);
        List<Expression> body = List.ofAll(context.expression()).map(this::build);

        Lambda func = new Lambda(new Location(context.getStart()), null, List.of(local), body);

        return new Call(location, null, buildQualifiedFunc(location, "List", "map"), List.of(collection, func));
    }

    private Expression build(StatementContext context) {
        return Match(context).of(
                Case(instanceOf(ImportStatementContext.class), this::build),
                Case(instanceOf(FunctionStatementContext.class), this::build)
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
                Case(instanceOf(ListLiteralExpressionContext.class), this::build),
                Case(instanceOf(IfExpressionContext.class), this::build),
                Case(instanceOf(ForExpressionContext.class), this::build)
        );
    }

    private Variable makeVariable(TerminalNode node) {
        return new Variable(new Location(node.getSymbol()), null, node.getText());
    }

    private Call buildQualifiedFunc(Location location, String module, String function) {
        Variable modVar = new Variable(location, null, module);
        Literal funVar = new Literal(location, function);

        Variable dotAccess = new Variable(location, null, ".");

        return new Call(location, null, dotAccess, List.of(modVar, funVar));
    }
}
