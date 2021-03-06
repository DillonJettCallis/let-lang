package com.redgear.let.ast;

import com.redgear.let.antlr.LetParser.*;
import com.redgear.let.types.*;
import javaslang.Tuple;
import javaslang.collection.*;
import javaslang.control.Option;
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

        return new Module(new Location(module.getStart()), expressions);
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

        if ("_".equals(var.getName())) {
            return ex;
        } else if (keywords.contains(var.getName())) {
            throw new RuntimeException("Can't assign to: " + var.getName() + " " + var.getLocation().print());
        } else {
            return new Assignment(new Location(context.getStart()), null, var, ex);
        }
    }

    private Lambda build(LambdaExpressionContext context) {
        var args = List.ofAll(context.maybeQualifiedVariable())
                .map(maybe -> {
                    var partial = makeVariable(maybe.LocalIdentifier());

                    if (maybe.argTypes != null) {
                        return partial.setTypeToken(buildTypeToken(maybe.argTypes));
                    } else {
                        return partial;
                    }
                });

        var body = List.ofAll(context.expression()).map(this::build);

        return new Lambda(new Location(context.getStart()), new SimpleFunctionTypeToken(args.map(Variable::getTypeToken), null), args, body);
    }

    private Assignment build(FunctionExpressionContext context) {
        var id = makeVariable(context.LocalIdentifier());

        var function = build(context.functionDeclaration());

        return  new Assignment(new Location(context.getStart()), function.getTypeToken(), id, function);
    }

    private Expression build(FunctionStatementContext context) {
        var exported = context.exported != null;
        var id = makeVariable(context.LocalIdentifier());

        var function = build(context.functionDeclaration());

        var assignment = new Assignment(new Location(context.getStart()), function.getTypeToken(), id, function);

        if (exported) {
            return new Export(new Location(context.getStart()), null, id.getName(), assignment);
        } else {
            return assignment;
        }
    }

    private Expression build(FunctionDeclarationContext context) {
        return Match(context).of(
                Case(instanceOf(SingleFunctionExpressionContext.class), ex -> build(ex.singleFunctionDeclaration())),
                Case(instanceOf(OverloadedFunctionExpressionContext.class), this::build)
        );
    }

    private Lambda build(SingleFunctionDeclarationContext context) {
        return Match(context).of(
                Case(instanceOf(SimpleFunctionExpressionContext.class), this::build),
                Case(instanceOf(GenericFunctionExpressionContext.class), this::build)
        );
    }

    private Lambda build(SimpleFunctionExpressionContext context) {
        var args = List.ofAll(context.qualifiedVariable())
                .map(maybe -> makeVariable(maybe.LocalIdentifier()).setTypeToken(buildTypeToken(maybe.argTypes)));

        var resultType = buildTypeToken(context.resultType);

        var body = List.ofAll(context.expression()).map(this::build);

        var functionType = new SimpleFunctionTypeToken(args.map(Variable::getTypeToken), resultType);

        return  new Lambda(new Location(context.getStart()), functionType, args, body);
    }

    private Lambda build(GenericFunctionExpressionContext context) {
        var typeParams = List.ofAll(context.typeParam).map(param -> new ParamaterTypeToken(param.getText()));
        var mappedParams = typeParams.toMap(param -> Tuple.of(param.getName(), param));

        var args = List.ofAll(context.qualifiedVariable())
                .map(maybe -> {
                    var typeToken = fillGenericParams(mappedParams, buildTypeToken(maybe.argTypes));

                    return makeVariable(maybe.LocalIdentifier()).setTypeToken(typeToken);
                });

        var resultType = fillGenericParams(mappedParams, buildTypeToken(context.resultType));

        var body = List.ofAll(context.expression()).map(this::build);

        var functionType = new GenericFunctionTypeToken(typeParams, args.map(Variable::getTypeToken), resultType);

        return  new Lambda(new Location(context.getStart()), functionType, args, body);
    }

    private TypeToken fillGenericParams(Map<String, ParamaterTypeToken> params, TypeToken typeToken) {
        if (typeToken instanceof NamedTypeToken) {
            var maybe = params.get(typeToken.getName());

            if (maybe.isDefined()) {
                return maybe.get();
            } else {
                return typeToken;
            }
        } else if (typeToken instanceof GenericTypeToken) {
            var genTypeToken = (GenericTypeToken) typeToken;
            return new GenericTypeToken(fillGenericParams(params, genTypeToken.getTypeConstructor()), genTypeToken.getTypeParams().map(p -> fillGenericParams(params, p)));
        } else if (typeToken instanceof GenericFunctionTypeToken) {
            var funTypeToken = (GenericFunctionTypeToken) typeToken;
            return new GenericFunctionTypeToken(funTypeToken.getTypeParameters(), funTypeToken.getArgTypes().map(a -> fillGenericParams(params, a)), fillGenericParams(params, funTypeToken.getResultType()));
        } else {
            return typeToken;
        }
    }

    private OverloadedFunction build(OverloadedFunctionExpressionContext context) {
        var overloads = List.ofAll(context.singleFunctionDeclaration()).map(this::build);

        return new OverloadedFunction(new Location(context.getStart()), new OverloadedFunctionTypeToken(overloads.map(Lambda::getTypeToken)), overloads);
    }

    private TypeToken buildTypeToken(TypeExpressionContext con) {
        return Match(con).of(
                Case(instanceOf(TypeIdentifierContext.class), context -> new NamedTypeToken(context.ModuleIdentifier().getText())),
                Case(instanceOf(TypeFunctionIdentifierContext.class), context -> {
                    var argTypes = List.ofAll(context.argTypes).map(this::buildTypeToken);

                    return new SimpleFunctionTypeToken(argTypes, buildTypeToken(context.resultType));
                }),
                Case(instanceOf(TypeGenericIdentifierContext.class), context -> {
                    var typeConstructor = buildTypeToken(context.type);
                    var typeParams = List.ofAll(context.typeParams).map(this::buildTypeToken);

                    return new GenericTypeToken(typeConstructor, typeParams);
                }), Case(instanceOf(TypeTupleIdentifierContext.class), context -> {
                    var typeParams = List.ofAll(context.typeExpression()).map(this::buildTypeToken);

                    return new GenericTypeToken(LiteralTypeToken.tupleTypeToken, typeParams);
                })
        );
    }

    private Call build(CallExpressionContext context) {
        List<Expression> args = List.ofAll(context.args).map(this::build);

        Expression method = build(context.method);

        return new Call(new Location(context.start), null, method, args);
    }

    private ModuleAccess build(ModuleAccessExpressionContext context) {
        var moduleName = context.ModuleIdentifier().getText();
        var localName = context.LocalIdentifier().getText();

        return new ModuleAccess(new Location(context.getStart()), null, moduleName, localName);
    }

    private Call build(UnaryOpExpressionContext context) {
        Variable var = new Variable(new Location(context.op), null, context.op.getText());

        Expression ex = build(context.expression());

        return new Call(new Location(context.getStart()), null, var, List.of(ex));
    }

    private Call build(UnaryNegOpExpressionContext context) {
        Variable var = new Variable(new Location(context.op), null, "*");

        Expression ex = build(context.expression());

        return new Call(new Location(context.getStart()), null, var, List.of(ex, new Literal(new Location(context.op), -1)));
    }

    private Call build(BinaryOpExpressionContext context) {
        Location opLocation = new Location(context.op);
        String op = context.op.getText();

        Expression opExpression = listOps.containsKey(op)
                ? buildQualifiedFunc(opLocation, "List", listOps.get(op).get())
                : new Variable(opLocation, null, op);

        Expression left = build(context.expression(0));
        Expression right = build(context.expression(1));

        return new Call(opLocation, null, opExpression, List.of(left, right));
    }

    private Branch build(BinaryAndOpExpressionContext context) {
        Location opLocation = new Location(context.op);

        Expression left = build(context.expression(0));
        Expression right = build(context.expression(1));

        return new Branch(opLocation, LiteralTypeToken.booleanTypeToken, left, right, new Literal(opLocation, false));
    }

    private Branch build(BinaryOrOpExpressionContext context) {
        Location opLocation = new Location(context.op);

        Expression left = build(context.expression(0));
        Expression right = build(context.expression(1));

        return new Branch(opLocation, LiteralTypeToken.booleanTypeToken, left, new Literal(opLocation, true), right);
    }

    private Parenthesized build(ParenthesizedExpressionContext context) {
        List<Expression> expressions = List.ofAll(context.expression()).map(this::build);

        return new Parenthesized(new Location(context.getStart()), null, expressions);
    }

    private Variable build(LocalIdentifierExpressionContext context) {
        return makeVariable(context.LocalIdentifier());
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

    private MapLiteral build(MapLiteralExpressionContext context) {
        var location = new Location(context.getStart());
        var keys = List.ofAll(context.keys).map(this::build);
        var values = List.ofAll(context.values).map(this::build);

        return new MapLiteral(location, null, keys, values);
    }

    private ListLiteral build(ListLiteralExpressionContext context) {
        var location = new Location(context.getStart());
        var expressions = List.ofAll(context.expression()).map(this::build);

        return new ListLiteral(location, null, expressions);
    }

    private TupleLiteral build(TupleLiteralExpressionContext context) {
        var location = new Location(context.getStart());
        var expressions = List.ofAll(context.expression()).map(this::build);

        return new TupleLiteral(location, null, expressions);
    }

    private Branch build(IfExpressionContext context) {
        Location location = new Location(context.getStart());
        Expression condition = build(context.condition);
        List<Expression> thenExs = List.ofAll(context.thenExpressions).map(this::build);
        List<Expression> elseExs = List.ofAll(context.elseExpressions).map(this::build);

        return new Branch(location, null, condition, new Parenthesized(new Location(context.getStart()), null, thenExs), new Parenthesized(new Location(context.getStart()), null, elseExs));
    }

    private Call build(ForExpressionContext context) {
        Location location = new Location(context.getStart());
        Variable local = makeVariable(context.LocalIdentifier());
        Expression collection = build(context.collection);
        List<Expression> body = List.ofAll(context.expression()).map(this::build);

        Lambda func = new Lambda(new Location(context.getStart()), new SimpleFunctionTypeToken(List.of(local.getTypeToken()), null), List.of(local), body);

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
                Case(instanceOf(LambdaExpressionContext.class), this::build),
                Case(instanceOf(FunctionExpressionContext.class), this::build),
                Case(instanceOf(CallExpressionContext.class), this::build),
                Case(instanceOf(ModuleAccessExpressionContext.class), this::build),
                Case(instanceOf(UnaryOpExpressionContext.class), this::build),
                Case(instanceOf(UnaryNegOpExpressionContext.class), this::build),
                Case(instanceOf(BinaryOpExpressionContext.class), this::build),
                Case(instanceOf(BinaryAndOpExpressionContext.class), this::build),
                Case(instanceOf(BinaryOrOpExpressionContext.class), this::build),
                Case(instanceOf(ParenthesizedExpressionContext.class), this::build),
                Case(instanceOf(LocalIdentifierExpressionContext.class), this::build),
                Case(instanceOf(IntLiteralExpressionContext.class), this::build),
                Case(instanceOf(FloatLiteralExpressionContext.class), this::build),
                Case(instanceOf(StringLiteralExpressionContext.class), this::build),
                Case(instanceOf(MapLiteralExpressionContext.class), this::build),
                Case(instanceOf(ListLiteralExpressionContext.class), this::build),
                Case(instanceOf(TupleLiteralExpressionContext.class), this::build),
                Case(instanceOf(IfExpressionContext.class), this::build),
                Case(instanceOf(ForExpressionContext.class), this::build)
        );
    }

    private Variable makeVariable(TerminalNode node) {
        return new Variable(new Location(node.getSymbol()), null, node.getText());
    }

    private ModuleAccess buildQualifiedFunc(Location location, String module, String function) {
        return new ModuleAccess(location, null, module, function);
    }
}
