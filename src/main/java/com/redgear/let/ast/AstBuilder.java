package com.redgear.let.ast;

import com.redgear.let.antlr.LetParser.*;
import com.redgear.let.eval.Interpreter;
import com.redgear.let.util.OptionalWrapper;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by LordBlackHole on 2017-01-07.
 */
public class AstBuilder {

    private static final Logger log = LoggerFactory.getLogger(AstBuilder.class);
    private static final Set<String> keywords = new HashSet<>(Arrays.asList("true", "false"));
    private final Interpreter interpreter;

    public AstBuilder(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public Module build(ModuleContext module) {
        List<Expression> expressions = module.statement().stream().map(this::build).collect(Collectors.toList());

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
        List<Variable> args = context.LocalIdentifier().stream().map(this::makeVariable).collect(Collectors.toList());

        List<Expression> expressions = context.expression().stream().map(this::build).collect(Collectors.toList());

        return new Lambda(new Location(context.getStart()), args, expressions);
    }

    private Call build(CallExpressionContext context) {
        List<Expression> args = context.args.stream().map(this::build).collect(Collectors.toList());

        Expression method = build(context.method);

        return new Call(new Location(context.start), method, args);
    }

    private Call build(ModuleAccessExpressionContext context) {
        Expression ex = build(context.expression());
        Literal var = new Literal(new Location(context.LocalIdentifier().getSymbol()), context.LocalIdentifier().getText());

        return new Call(new Location(context.getStart()), new Variable(var.getLocation(), "."), Arrays.asList(ex, var));
    }

    private Call build(UnaryOpExpressionContext context) {
        Variable var = new Variable(new Location(context.op), context.op.getText());

        Expression ex = build(context.expression());

        return new Call(new Location(context.getStart()), var, Collections.singletonList(ex));
    }

    private Call build(BinaryOpExpressionContext context) {
        Variable var = new Variable(new Location(context.op), context.op.getText());

        Expression left  = build(context.expression(0));
        Expression right = build(context.expression(1));

        return new Call(new Location(context.op), var, Arrays.asList(left, right));
    }

    private Parenthesized build(ParenthesizedExpressionContext context) {
        List<Expression> expressions = context.expression().stream().map(this::build).collect(Collectors.toList());

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
        List<Expression> expressions = context.expression().stream().map(this::build).collect(Collectors.toList());

        return new Call(location, buildQualifiedFunc(location, "Map", "build"), expressions);
    }

    private Call build(ListLiteralExpressionContext context) {
        Location location = new Location(context.getStart());
        List<Expression> expressions = context.expression().stream().map(this::build).collect(Collectors.toList());

        return new Call(location, buildQualifiedFunc(location, "List", "build"), expressions);
    }

    private Expression build(StatementContext context) {

        return new OptionalWrapper<>(build(context, this::build, ImportStatementContext.class))
                .orElse(() -> build(context, this::build, ExportStatementContext.class))
                .orElse(() -> build(context, this::build, ExpressionStatementContext.class))
                .get().orElseThrow(() -> new RuntimeException("No matching builder for StatementContext of type: " + context.getClass()));

    }

    private Expression build(ExpressionContext context) {

        return new OptionalWrapper<>(build(context, this::build, AssignmentExpressionContext.class))
                .orElse(() -> build(context, this::build, FunctionExpressionContext.class))
                .orElse(() -> build(context, this::build, CallExpressionContext.class))
                .orElse(() -> build(context, this::build, ModuleAccessExpressionContext.class))
                .orElse(() -> build(context, this::build, UnaryOpExpressionContext.class))
                .orElse(() -> build(context, this::build, BinaryOpExpressionContext.class))
                .orElse(() -> build(context, this::build, ParenthesizedExpressionContext.class))
                .orElse(() -> build(context, this::build, LocalIdentifierExpressionContext.class))
                .orElse(() -> build(context, this::build, ModuleIdentifierExpressionContext.class))
                .orElse(() -> build(context, this::build, IntLiteralExpressionContext.class))
                .orElse(() -> build(context, this::build, FloatLiteralExpressionContext.class))
                .orElse(() -> build(context, this::build, StringLiteralExpressionContext.class))
                .orElse(() -> build(context, this::build, MapLiteralExpressionContext.class))
                .orElse(() -> build(context, this::build, ListLiteralExpressionContext.class))
                .get().orElseThrow(() -> new RuntimeException("No matching builder for ExpressionContext of type: " + context.getClass()));

    }

    private Variable makeVariable(TerminalNode node) {
        return new Variable(new Location(node.getSymbol()), node.getText());
    }

    private Call buildQualifiedFunc(Location location, String module, String function) {
        Variable modVar = new Variable(location, module);
        Literal funVar = new Literal(location, function);

        Variable dotAccess = new Variable(location, ".");

        return new Call(location, dotAccess, Arrays.asList(modVar, funVar));
    }

    private <T extends ParserRuleContext> Optional<Expression> build(ParserRuleContext context, Function<T, Expression> func, Class<T> clazz) {

        if(clazz.isInstance(context)){
            return Optional.of(func.apply(clazz.cast(context)));
        } else {
            return Optional.empty();
        }
    }

}
