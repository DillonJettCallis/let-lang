package com.redgear.let.types;

import com.redgear.let.ast.*;
import com.redgear.let.ast.Module;
import com.redgear.let.load.Loader;

import java.util.HashMap;
import java.util.Map;

import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.Predicates.instanceOf;

public class TypeChecker {

    private final Map<String, TypeToken> knownTypes = new HashMap<>();
    private final Map<String, ModuleTypeScope> knownModules = new HashMap<>();
    private final LibraryTypeScope typeScope;
    private final Loader loader;

    public TypeChecker(LibraryTypeScope typeScope, Loader loader) {
        this.typeScope = typeScope;
        this.loader = loader;
        this.knownModules.putAll(typeScope.getCoreModules());
        this.knownTypes.put("String", LiteralTypeToken.stringTypeToken);
        this.knownTypes.put("Int", LiteralTypeToken.intTypeToken);
        this.knownTypes.put("Float", LiteralTypeToken.floatTypeToken);
    }

    private TypeToken resolveTypeToken(TypeToken typeToken) {
        if (typeToken instanceof NamedTypeToken) {
            String name = typeToken.getName();
            return knownTypes.get(name);
        } else if (typeToken instanceof FunctionTypeToken) {
            var funcTypeToken = (FunctionTypeToken) typeToken;
            return new FunctionTypeToken(funcTypeToken.getArgTypes().map(this::resolveTypeToken), resolveTypeToken(funcTypeToken.getResultType()));
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
        var function = visit(typeScope, ex.getMethod());
        var funcType = function.getTypeToken();
        var args = ex.getArguments().map(e -> visit(typeScope, e));
        var argTypes = args.map(Expression::getTypeToken);

        if (funcType instanceof FunctionTypeToken) {
            var funcTypeToken = (FunctionTypeToken) funcType;

            if (funcTypeToken.getArgTypes().equals(argTypes)) {
                return new Call(ex.getLocation(), funcTypeToken.getResultType(), function, args);
            } else {
                var expected = funcTypeToken.getArgTypes().map(TypeToken::getName).mkString(", ");
                var actual = argTypes.map(TypeToken::getName).mkString(", ");

                throw new RuntimeException("Attempt to call function with wrong types of arguments. Expected: (" + expected + "), found: (" + actual + "). " + ex.getLocation().print());
            }
        } if (funcType instanceof DynamicFunctionTypeToken) {
            var dynamicFunction = (DynamicFunctionTypeToken) funcType;
            var resultType = dynamicFunction.getResultType(argTypes);

            if (resultType == null) {
                var actual = argTypes.map(TypeToken::getName).mkString(", ");

                throw new RuntimeException("Attempt to call function with wrong types of arguments. Function: (" + dynamicFunction.getName() + "), found: (" + actual + "). " + ex.getLocation().print());
            } else {
                return new Call(ex.getLocation(), resultType, function, args);
            }

        } else {
            throw new RuntimeException("Attempt to call a non-function: " + ex.getLocation().print());
        }
    }

    public Export visit(LocalTypeScope typeScope, Export ex) {
        var body = visit(typeScope, ex.getExpression());
        typeScope.exportType(ex.getName(), body.getTypeToken());
        return ex.setBody(body);
    }


    public Import visit(LocalTypeScope typeScope, Import ex) {
        var maybeModule = typeScope.importModule(ex.getAlias());

        if (maybeModule == null) {
            var module = loadModule(ex.getPath());
            typeScope.declareImport(ex.getAlias(), module);
        }

        return ex;
    }

    private ModuleTypeScope loadModule(String name){
        if (this.knownModules.containsKey(name)) {
            return this.knownModules.get(name);
        } else {
            var module = loader.loadModule(name);
            var scope = visit(module);
            this.knownModules.put(name, scope);
            return scope;
        }
    }


    public Lambda visit(LocalTypeScope typeScope, Lambda ex) {
        var innerScope = new LocalTypeScope(typeScope);

        var args = ex.getVariables()
                .map(arg -> arg.setTypeToken(resolveTypeToken(arg.getTypeToken())));
        args.forEach(arg -> innerScope.declareType(arg.getName(), sureType(arg.getTypeToken())));

        var body = ex.getStatements().map(e -> visit(innerScope, e));
        var bodyTypeToken = body.last().getTypeToken();

        var resultType = resolveTypeToken(ex.getTypeToken().getResultType());

        if (bodyTypeToken != null && resultType != null && !resultType.equals(bodyTypeToken)) {
            throw new RuntimeException("Function declared type differs from returned type. Declared: " + ex.getTypeToken().getName() + ", returns: " + bodyTypeToken.getName() + " " + ex.getLocation().print());
        }

        return new Lambda(ex.getLocation(), new FunctionTypeToken(args.map(Variable::getTypeToken), bodyTypeToken), args, body);
    }


    public Literal visit(LocalTypeScope typeScope, Literal ex) {
        return ex;
    }


    public ModuleTypeScope visit(Module module) {
        var moduleScope = new ModuleTypeScope(typeScope);
        var localScope = new LocalTypeScope(moduleScope);

        module.getExpressions().forEach(ex -> visit(localScope, ex));

        return moduleScope;
    }

    public ModuleAccess visit(LocalTypeScope typeScope, ModuleAccess ex) {
        var moduleScope = typeScope.importModule(ex.getModule());

        if (moduleScope != null) {
            var local = moduleScope.getType(ex.getAccess());

            if (local != null) {
                return ex.setTypeToken(local);
            } else {
                throw new RuntimeException("Module " + ex.getModule() + " has no export named " + ex.getAccess() + " " + ex.getLocation().print());
            }
        } else {
            throw new RuntimeException("No module named " + ex.getModule() + " imported in current scope " + ex.getLocation().print());
        }
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
                Case(instanceOf(Variable.class), ex -> visit(typeScope, ex)),
                Case(instanceOf(ModuleAccess.class), ex -> visit(typeScope, ex))
        );
    }
}
