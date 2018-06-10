package com.redgear.let.types;

import com.redgear.let.ast.*;
import com.redgear.let.ast.Module;

import java.util.HashMap;
import java.util.Map;

import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.Predicates.instanceOf;

public class TypeChecker {

    private final Map<String, TypeToken> knownTypes = new HashMap<>();

    public TypeChecker() {
        this.knownTypes.put("String", LiteralTypeToken.stringTypeToken);
        this.knownTypes.put("Int", LiteralTypeToken.intTypeToken);
        this.knownTypes.put("Float", LiteralTypeToken.floatTypeToken);
    }

    private TypeToken resolveTypeToken(TypeToken typeToken) {
        if (typeToken instanceof NamedTypeToken) {
            String name = ((NamedTypeToken) typeToken).getName();
            return knownTypes.get(name);
        } else {
            return typeToken;
        }
    }

    private TypeToken sureType(TypeToken typeToken) {
        return typeToken == null ? LiteralTypeToken.nullTypeToken : typeToken;
    }

    public Assignment visit(LocalTypeScope typeScope, Assignment ex) {
        var body = visit(typeScope, ex.getBody());
        var sureType = sureType(body.getTypeToken());
        typeScope.declareType(ex.getVar().getName(), sureType);
        return ex.setBody(body);
    }


    public Call visit(LocalTypeScope typeScope, Call ex) {
        ex.getArguments().forEach(e -> visit(typeScope, e));
        return ex;
    }


    public Export visit(LocalTypeScope typeScope, Export ex) {
        var body = visit(typeScope, ex.getExpression());
        typeScope.exportType(ex.getName(), body.getTypeToken());
        return ex.setBody(body);
    }


    public Import visit(LocalTypeScope typeScope, Import ex) {
        // TODO: check imports
        typeScope.declareType(ex.getAlias(), LiteralTypeToken.nullTypeToken);
        return ex;
    }


    public Lambda visit(LocalTypeScope typeScope, Lambda ex) {
        var innerScope = new LocalTypeScope(typeScope);
        var resultType = resolveTypeToken(ex.getTypeToken());

        ex.getVariables()
                .map(arg -> arg.setTypeToken(resolveTypeToken(arg.getTypeToken())))
                .forEach(arg -> innerScope.declareType(arg.getName(), sureType(arg.getTypeToken())));

        var body = ex.getStatements().map(e -> visit(innerScope, e));

        return ex.setTypeToken(resultType).setBody(body);
    }


    public Literal visit(LocalTypeScope typeScope, Literal ex) {
        return ex;
    }


    public ModuleTypeScope visit(LibraryTypeScope typeScope, Module module) {
        var moduleScope = new ModuleTypeScope(typeScope);
        var localScope = new LocalTypeScope(moduleScope);

        module.getExpressions().forEach(ex -> visit(localScope, ex));

        return moduleScope;
    }


    public Parenthesized visit(LocalTypeScope typeScope, Parenthesized ex) {
        var body = ex.getExpressions().map(e -> visit(typeScope, e));
        return new Parenthesized(ex.getLocation(), body.last().getTypeToken(), body);
    }


    public Variable visit(LocalTypeScope typeScope, Variable ex) {
        var typeToken = typeScope.getType(ex.getName());
        if (typeToken == null) {
            throw new RuntimeException("Attempt to use undeclared variable: " + ex.getName() + " " + ex.getLocation().print());
        }

        return ex.setTypeToken(typeToken);
    }

    public Expression visit(LocalTypeScope typeScope, Expression expression) {
        return Match(expression).of(
                Case(instanceOf(Assignment.class), ex -> visit(typeScope, ex)),
                Case(instanceOf(Call.class), ex -> visit(typeScope, ex)),
                Case(instanceOf(Export.class), ex -> visit(typeScope, ex)),
                Case(instanceOf(Import.class), ex -> visit(typeScope, ex)),
                Case(instanceOf(Lambda.class), ex -> visit(typeScope, ex)),
                Case(instanceOf(Literal.class), ex -> visit(typeScope, ex)),
                Case(instanceOf(Parenthesized.class), ex -> visit(typeScope, ex)),
                Case(instanceOf(Variable.class), ex -> visit(typeScope, ex))
        );
    }
}
