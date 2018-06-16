package com.redgear.let.types;

import com.redgear.let.ast.*;
import com.redgear.let.ast.Module;
import com.redgear.let.lib.ModuleDefinition;
import com.redgear.let.load.Loader;
import javaslang.collection.List;

import java.util.HashMap;
import java.util.Map;

import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.Predicates.instanceOf;

public class TypeChecker implements Loader {

    private final Map<String, ModuleTypeScope> importedModules = new HashMap<>();
    private final LibraryTypeScope typeScope;
    private final Loader loader;

    public TypeChecker(LibraryTypeScope typeScope, Loader loader) {
        this.typeScope = typeScope;
        this.loader = loader;
    }

    @Override
    public Module loadModule(String name) {
        var module = loader.loadModule(name);
        var moduleScope = new ModuleTypeScope(typeScope);
        var localScope = new LocalTypeScope(moduleScope);

        var body = module.getExpressions().map(ex -> visit(localScope, ex));

        importedModules.put(name, moduleScope);

        return new Module(module.getLocation(), body);
    }

    public void loadLibModule(ModuleDefinition moduleDefinition) {
        var moduleScope = new ModuleTypeScope(typeScope);
        moduleDefinition.buildTypes(moduleScope);
        importedModules.put(moduleDefinition.getName(), moduleScope);
    }

    private TypeToken resolveTypeToken(TypeToken typeToken) {
        if (typeToken instanceof NamedTypeToken) {
            String name = typeToken.getName();
            return typeScope.getType(name);
        } else if (typeToken instanceof OverloadedFunctionTypeToken) {
            return new OverloadedFunctionTypeToken( ((OverloadedFunctionTypeToken) typeToken).getImplementations().map(this::resolveTypeToken));
        } else if (typeToken instanceof GenericFunctionTypeToken) {
            var funcTypeToken = (GenericFunctionTypeToken) typeToken;
            return new GenericFunctionTypeToken(funcTypeToken.getTypeParameters(), funcTypeToken.getArgTypes().map(this::resolveTypeToken), resolveTypeToken(funcTypeToken.getResultType()));
        } else if (typeToken instanceof SimpleFunctionTypeToken) {
            return resolveTypeToken((SimpleFunctionTypeToken) typeToken);
        } else {
            return typeToken;
        }
    }

    private SimpleFunctionTypeToken resolveTypeToken(SimpleFunctionTypeToken funcTypeToken) {
        return new SimpleFunctionTypeToken(funcTypeToken.getArgTypes().map(this::resolveTypeToken), resolveTypeToken(funcTypeToken.getResultType()));
    }

    private TypeToken sureType(TypeToken typeToken) {
        return typeToken == null ? LiteralTypeToken.nullTypeToken : typeToken;
    }

    private Assignment visit(LocalTypeScope typeScope, Assignment ex) {
        var body = visit(typeScope, ex.getBody());
        var sureType = sureType(body.getTypeToken());
        typeScope.declareType(ex.getVar().getName(), sureType);
        return ex.setBody(body);
    }


    private Call visit(LocalTypeScope typeScope, Call ex) {
        var function = visit(typeScope, ex.getMethod());
        var funcType = function.getTypeToken();

        if (funcType instanceof FunctionTypeToken) {
            var genTypeToken = (FunctionTypeToken) funcType;

            var needsHigherOrder = ex.getArguments().find(e -> e instanceof Lambda && ((Lambda) e).getVariables().find(v -> v.getTypeToken() == null).isDefined()).isDefined();

            if (needsHigherOrder) {
                return resolveHigherOrderFunctionCall(typeScope, ex, function, genTypeToken);
            } else {
                var args = ex.getArguments().map(e -> visit(typeScope, e));
                var argTypes = args.map(Expression::getTypeToken);

                return buildCall(ex, function, genTypeToken, args, argTypes);
            }
        } else {
            throw new RuntimeException("Attempt to call a non-function: " + ex.getLocation().print());
        }
    }

    private Call resolveHigherOrderFunctionCall(LocalTypeScope typeScope, Call ex, Expression function, FunctionTypeToken genTypeToken) {
        var args = ex.getArguments().map(e -> {
            if (e instanceof Lambda) {
                var simple = (Lambda) e;
                var vars = simple.getVariables().map(v -> {
                    if (v.getTypeToken() == null) {
                        return v;
                    } else {
                        return v.setTypeToken(resolveTypeToken(v.getTypeToken()));
                    }
                });

                return new Lambda(simple.getLocation(), (FunctionTypeToken) resolveTypeToken(simple.getTypeToken()), vars, simple.getStatements());
            } else {
                return visit(typeScope, e);
            }
        });
        var argTypes = args.map(Expression::getTypeToken);

        var resolvedType = genTypeToken.getResolvedType(argTypes);

        var resolvedArgs = args.zip(resolvedType.getArgTypes()).map(pair -> {
            if (pair._1 instanceof Lambda && pair._2 instanceof SimpleFunctionTypeToken) {
                var l = (Lambda) pair._1;
                var f = (SimpleFunctionTypeToken) pair._2;
                var vars = l.getVariables().zip(f.getArgTypes()).map(param -> {
                    if (param._1.getTypeToken() == null) {
                        return new Variable(param._1.getLocation(), param._2, param._1.getName());
                    } else {
                        return param._1;
                    }
                });

                return visit(typeScope, new Lambda(l.getLocation(), l.getTypeToken(), vars, l.getStatements()));
            } else {
                return pair._1;
            }
        });

        return buildCall(ex, function, genTypeToken, args, resolvedArgs.map(Expression::getTypeToken));
    }

    private Call buildCall(Call ex, Expression function, FunctionTypeToken genTypeToken, List<Expression> args, List<TypeToken> argTypes) {
        var resultType = genTypeToken.getResolvedType(argTypes).getResultType();

        if (resultType != null) {
            return new Call(ex.getLocation(), resultType, function, args);
        } else {
            var expected = genTypeToken.getName();
            var actual = argTypes.map(TypeToken::getName).mkString(", ");

            throw new RuntimeException("Attempt to call function with wrong types of arguments. Function type: " + expected + ", found args: (" + actual + "). " + ex.getLocation().print());
        }
    }

    private Export visit(LocalTypeScope typeScope, Export ex) {
        var body = visit(typeScope, ex.getExpression());
        typeScope.exportType(ex.getName(), body.getTypeToken());
        return ex.setBody(body);
    }


    private Import visit(LocalTypeScope typeScope, Import ex) {
        var maybeModule = typeScope.importModule(ex.getAlias());

        if (maybeModule == null) {
            if (importedModules.containsKey(ex.getPath())) {
                typeScope.declareImport(ex.getAlias(), importedModules.get(ex.getPath()));
            } else {
                var maybeAgain = loadModule(ex.getPath());
                if (maybeAgain != null) {
                    typeScope.declareImport(ex.getAlias(), importedModules.get(ex.getPath()));
                } else {
                    throw new RuntimeException("Cannot find module: " + ex.getPath() + " " + ex.getLocation().print());
                }
            }
        } else {
            throw new RuntimeException("Redundant import of module " + ex.getPath() + " " + ex.getLocation().print());
        }

        return ex;
    }

    private Lambda visit(LocalTypeScope typeScope, Lambda ex) {
        var innerScope = new LocalTypeScope(typeScope);

        var functionType = (SimpleFunctionTypeToken) resolveTypeToken(ex.getTypeToken());

        var args = ex.getVariables()
                .map(arg -> arg.setTypeToken(resolveTypeToken(arg.getTypeToken())));
        args.forEach(arg -> innerScope.declareType(arg.getName(), sureType(arg.getTypeToken())));

        // TODO: Allow and verify the body of Generic functions
        var body = ex.getStatements().map(e -> visit(innerScope, e));
        var bodyTypeToken = body.last().getTypeToken();

        var resultType = functionType.getResultType();

        if (resultType != null && !resultType.equals(bodyTypeToken)) {
            throw new RuntimeException("Function declared type differs from returned type. Declared: " + ex.getTypeToken().getName() + ", returns: " + bodyTypeToken.getName() + " " + ex.getLocation().print());
        }

        return new Lambda(ex.getLocation(), functionType.setResultType(bodyTypeToken), args, body);
    }


    private Literal visit(LocalTypeScope typeScope, Literal ex) {
        return ex;
    }

    private ModuleAccess visit(LocalTypeScope typeScope, ModuleAccess ex) {
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


    private Parenthesized visit(LocalTypeScope typeScope, Parenthesized ex) {
        var body = ex.getExpressions().map(e -> visit(typeScope, e));
        return new Parenthesized(ex.getLocation(), body.last().getTypeToken(), body);
    }


    private Variable visit(LocalTypeScope typeScope, Variable ex) {
        var typeToken = typeScope.getType(ex.getName());
        if (typeToken == null) {
            throw new RuntimeException("Attempt to use undeclared variable: " + ex.getName() + " " + ex.getLocation().print());
        }

        return ex.setTypeToken(typeToken);
    }

    private MapLiteral visit(LocalTypeScope typeScope, MapLiteral ex) {
        var keys = ex.getKeys().map(key -> visit(typeScope, key));
        var values = ex.getValues().map(value -> visit(typeScope, value));

        var keyType = singleType(keys.map(Expression::getTypeToken));
        var valueType = singleType(values.map(Expression::getTypeToken));

        if (keyType == null || valueType == null) {
            throw new RuntimeException("Mixed maps are not yet supported " + ex.getLocation().print());
        } else {
            return new MapLiteral(ex.getLocation(), LiteralTypeToken.mapTypeToken.construct(List.of(keyType, valueType)), keys, values);
        }
    }

    private ListLiteral visit(LocalTypeScope typeScope, ListLiteral ex) {
        var values = ex.getValues().map(value -> visit(typeScope, value));

        var valueType = singleType(values.map(Expression::getTypeToken));

        if (valueType == null) {
            throw new RuntimeException("Mixed lists are not yet supported " + ex.getLocation().print());
        } else {
            return new ListLiteral(ex.getLocation(), LiteralTypeToken.listTypeToken.construct(List.of(valueType)), values);
        }
    }

    private TupleLiteral visit(LocalTypeScope typeScope, TupleLiteral ex) {
        var values = ex.getValues().map(value -> visit(typeScope, value));
        var valueTypes = values.map(Expression::getTypeToken);

        return new TupleLiteral(ex.getLocation(), new GenericTypeToken(LiteralTypeToken.tupleTypeToken, valueTypes), values);
    }

    private Branch visit(LocalTypeScope typeScope, Branch ex) {
        var condition = visit(typeScope, ex.getCondition());

        if (!LiteralTypeToken.booleanTypeToken.equals(condition.getTypeToken())) {
            throw new RuntimeException("Required boolean expression, found: " + condition.getTypeToken().getName() + " " + ex.getLocation().print());
        }

        var thenScope = new LocalTypeScope(typeScope);
        var thenBlock = visit(thenScope, ex.getThenBlock());

        var elseScope = new LocalTypeScope(typeScope);
        var elseBlock = visit(elseScope, ex.getElseBlock());

        if (ex.getTypeToken() == null) {
            if (!thenBlock.getTypeToken().equals(elseBlock.getTypeToken())) {
                throw new RuntimeException("If statement then and else block return different types. then: " + thenBlock.getTypeToken().getName() + ", else: " + elseBlock.getTypeToken().getName() + " " + ex.getLocation().print());
            } else {
                return new Branch(ex.getLocation(), thenBlock.getTypeToken(), condition, thenBlock, elseBlock);
            }
        } else {
            if (!LiteralTypeToken.booleanTypeToken.equals(thenBlock.getTypeToken())) {
                throw new RuntimeException("Required boolean expression, found: " + condition.getTypeToken().getName() + " " + thenBlock.getLocation().print());
            }

            if (!LiteralTypeToken.booleanTypeToken.equals(elseBlock.getTypeToken())) {
                throw new RuntimeException("Required boolean expression, found: " + condition.getTypeToken().getName() + " " + elseBlock.getLocation().print());
            }

            return new Branch(ex.getLocation(), ex.getTypeToken(), condition, thenBlock, elseBlock);
        }
    }

    private TypeToken singleType(List<TypeToken> source) {
        if (source.isEmpty()) {
            return null;
        } else {
            return source.tail().foldLeft(source.head(), (prev, next) -> {
                if (prev == null) {
                    return null;
                } else {
                    if (prev.equals(next)) {
                        return prev;
                    } else {
                        return null;
                    }
                }
            });
        }
    }

    private Expression visit(LocalTypeScope typeScope, Expression expression) {
        return Match(expression).of(
                Case(instanceOf(Assignment.class), ex -> visit(typeScope, ex)),
                Case(instanceOf(Call.class), ex -> visit(typeScope, ex)),
                Case(instanceOf(Export.class), ex -> visit(typeScope, ex)),
                Case(instanceOf(Import.class), ex -> visit(typeScope, ex)),
                Case(instanceOf(Lambda.class), ex -> visit(typeScope, ex)),
                Case(instanceOf(Literal.class), ex -> visit(typeScope, ex)),
                Case(instanceOf(Parenthesized.class), ex -> visit(typeScope, ex)),
                Case(instanceOf(Variable.class), ex -> visit(typeScope, ex)),
                Case(instanceOf(MapLiteral.class), ex -> visit(typeScope, ex)),
                Case(instanceOf(ListLiteral.class), ex -> visit(typeScope, ex)),
                Case(instanceOf(TupleLiteral.class), ex -> visit(typeScope, ex)),
                Case(instanceOf(Branch.class), ex -> visit(typeScope, ex)),
                Case(instanceOf(ModuleAccess.class), ex -> visit(typeScope, ex))
        );
    }
}
