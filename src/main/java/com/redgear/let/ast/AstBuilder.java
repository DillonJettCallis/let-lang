package com.redgear.let.ast;

import com.redgear.let.antlr.LetParser.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by LordBlackHole on 2017-01-07.
 */
public class AstBuilder {

    private static final Logger log = LoggerFactory.getLogger(AstBuilder.class);


    public Expression build(ModuleContext module) {
        List<Expression> expressions = module.statement().stream().map(this::build).collect(Collectors.toList());

        return new Module(new Location(module.getStart()), expressions);
    }

    private Expression build(ExpressionStatementContext expression) {
        return build(expression.expression());
    }

    private Expression build(ImportStatementContext context) {
        PortInContext portIn = context.portIn();

        String id = portIn.ModuleIdentifier().getText();
        String file = portIn.StringLiteral().getText();

        return new Import(new Location(context.getStart()), id, file);
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

    private Assignment build(AssignmentExpressionContext context) {
        Variable var = makeVariable(context.LocalIdentifier());

        Expression ex = build(context.expression());

        return new Assignment(new Location(context.getStart()), var, ex);
    }

    private Func build(FunctionExpressionContext context) {
        List<Variable> args = context.LocalIdentifier().stream().map(this::makeVariable).collect(Collectors.toList());

        List<Expression> expressions = context.expression().stream().map(this::build).collect(Collectors.toList());

        return new Func(new Location(context.getStart()), args, expressions);
    }

    private Call build(CallExpressionContext context) {
        List<Expression> expressions = context.expression().stream().map(this::build).collect(Collectors.toList());

        Expression method = expressions.get(0);

        List<Expression> args = expressions.subList(0, expressions.size() - 1);

        return new Call(new Location(context.start), method, args);
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

        return new Call(new Location(context.getStart()), var, Arrays.asList(left, right));
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
        return new Literal(new Location(context.getStart()), context.StringLiteral().getText());
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
                .orElse(() -> build(context, this::build, UnaryOpExpressionContext.class))
                .orElse(() -> build(context, this::build, BinaryOpExpressionContext.class))
                .orElse(() -> build(context, this::build, ParenthesizedExpressionContext.class))
                .orElse(() -> build(context, this::build, LocalIdentifierExpressionContext.class))
                .orElse(() -> build(context, this::build, ModuleIdentifierExpressionContext.class))
                .orElse(() -> build(context, this::build, IntLiteralExpressionContext.class))
                .orElse(() -> build(context, this::build, FloatLiteralExpressionContext.class))
                .orElse(() -> build(context, this::build, StringLiteralExpressionContext.class))
                .get().orElseThrow(() -> new RuntimeException("No matching builder for ExpressionContext of type: " + context.getClass()));

    }

    private Variable makeVariable(TerminalNode node) {
        return new Variable(new Location(node.getSymbol()), node.getText());
    }

    private <T extends ParserRuleContext> Optional<Expression> build(ParserRuleContext context, Function<T, Expression> func, Class<T> clazz) {

        if(clazz.isInstance(context)){
            return Optional.of(func.apply(clazz.cast(context)));
        } else {
            return Optional.empty();
        }
    }

    private static class OptionalWrapper<T> {

        private final Optional<T> optional;


        private OptionalWrapper(Optional<T> optional) {
            this.optional = optional;
        }


        public OptionalWrapper<T> orElse(Supplier<Optional<T>> source) {
            if(optional.isPresent())
                return this;
            else
                return new OptionalWrapper<>(source.get());
        }

        public Optional<T> get() {
            return optional;
        }

    }
}