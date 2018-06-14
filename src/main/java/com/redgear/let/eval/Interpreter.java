package com.redgear.let.eval;

import com.redgear.let.ast.*;
import com.redgear.let.ast.Module;
import com.redgear.let.lib.ModuleDefinition;
import com.redgear.let.load.Loader;
import com.redgear.let.types.ModuleTypeScope;
import javaslang.Tuple;
import javaslang.collection.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static javaslang.API.Case;
import static javaslang.API.Match;
import static javaslang.Predicates.instanceOf;

public class Interpreter {

    private static final Logger log = LoggerFactory.getLogger(Interpreter.class);

    private final LibraryScope libraryScope;
    private final Map<String, ModuleScope> modules = new HashMap<>();
    private final Caller caller;
    private final Loader loader;

    public Interpreter(LibraryScope libraryScope, Loader loader) {
        this.libraryScope = libraryScope;
        this.loader = loader;
        this.caller = new Caller(this);
    }
    public void run(String mainModule) {
        var scope = loadModule(mainModule);

        var main = scope.getValue("main");

        if (main instanceof Func) {
            caller.call(new LocalScope(scope), (Func) main, List.of());
        } else {
            throw new RuntimeException("No main() function in module: " + mainModule);
        }
    }

    public ModuleScope loadModule(String fileName) {
        if (modules.containsKey(fileName)) {
            return modules.get(fileName);
        } else {
            Module module = loader.loadModule(fileName);
            var moduleScope = new ModuleScope(libraryScope);
            eval(new LocalScope(moduleScope), module);
            modules.put(fileName, moduleScope);
            return moduleScope;
        }
    }

    public void loadLibModule(ModuleDefinition moduleDefinition) {
        var moduleScope = new ModuleScope(libraryScope);
        moduleDefinition.buildLibrary(this, moduleScope);
        modules.put(moduleDefinition.getName(), moduleScope);
    }

    private Object eval(LocalScope scope, ModuleAccess ex) {
        var module = scope.importModule(ex.getModule());

        if (module != null) {
            var value = module.getValue(ex.getAccess());

            if (value == null) {
                throw new RuntimeException("Function " + ex.getModule() + "." + ex.getAccess() + " is not defined. " + ex.getLocation().print());
            } else {
                return value;
            }
        } else {
            throw new RuntimeException("Module " + ex.getModule() + " was not found. " + ex.getLocation().print());
        }
    }

    private Object eval(LocalScope scope, Assignment expression) {
        Object value = eval(scope, expression.getBody());
        scope.putValue(expression.getVar().getName(), value);
        log.info("let {} = {}", expression.getVar().getName(), value);
        return value;
    }

    private Object eval(LocalScope scope, Call call) {
        Object obj = eval(scope, call.getMethod());

        if (obj == null) {
            throw new RuntimeException("Undefined method " + call.getMethod().getLocation().print());
        }

        if(obj instanceof Func) {
            return caller.call(scope, (Func) obj, call.getArguments());
        } else {
            throw new RuntimeException("Unknown function type: " + obj.getClass());
        }
    }

    private Object eval(LocalScope scope, Export expression) {
        Object value = eval(scope, expression.getExpression());

        scope.exportValue(expression.getName(), value);

        return value;
    }

    private Object eval(LocalScope scope, Import expression) {
        ModuleScope importScope = loadModule(expression.getPath());

        scope.declareImport(expression.getAlias(), importScope);

        return importScope;
    }

    private DefinedFunc eval(LocalScope scope, Lambda expression) {
        return (args) -> {

            LocalScope inner = new LocalScope(scope);
            List<Variable> variables = expression.getVariables();

            variables.zip(args).forEach(pair -> inner.putValue(pair._1.getName(), pair._2));

            return expression.getStatements().foldLeft(null, (prev, ex) -> eval(inner, ex));
        };
    }

    private Object eval(Literal expression) {
        return expression.getValue();
    }

    private void eval(LocalScope scope, Module expression) {
        expression.getExpressions().forEach(ex -> eval(scope, ex));
    }

    private Object eval(LocalScope scope, Parenthesized expression) {
        LocalScope inner = expression.isNeedsScope() ? new LocalScope(scope) : scope;

        return expression.getExpressions().foldLeft(null, (prev, ex) -> eval(inner, ex));
    }

    private Object eval(LocalScope scope, Variable expression) {
        return scope.getValue(expression.getName());
    }

    private Object eval(LocalScope scope, MapLiteral expression) {
        return expression.getKeys().zip(expression.getValues()).toMap(pair -> Tuple.of(eval(scope, pair._1), eval(scope, pair._2)));
    }

    private Object eval(LocalScope scope, ListLiteral expression) {
        return expression.getValues().map(ex -> eval(scope, ex));
    }

    private Object eval(LocalScope scope, TupleLiteral expression) {
        return expression.getValues().map(ex -> eval(scope, ex));
    }

    private Object eval(LocalScope scope, Branch expression) {
        var condition = eval(scope, expression.getCondition());

        if (condition instanceof Boolean) {
            boolean cond = (Boolean) condition;
            var blockScope = new LocalScope(scope);

            if (cond) {
                return eval(blockScope, expression.getThenBlock());
            } else {
                return eval(blockScope, expression.getElseBlock());
            }
        } else {
            throw new RuntimeException("Type error. Expected boolean but found: " + (condition == null ? "null" : condition.getClass()) + " " + expression.getCondition().getLocation());
        }
    }

    public Object eval(LocalScope scope, Expression expression) {
        return Match(expression).of(
                Case(instanceOf(Assignment.class), ex -> eval(scope, ex)),
                Case(instanceOf(Call.class), ex -> eval(scope, ex)),
                Case(instanceOf(Export.class), ex -> eval(scope, ex)),
                Case(instanceOf(Import.class), ex -> eval(scope, ex)),
                Case(instanceOf(Lambda.class), ex -> eval(scope, ex)),
                Case(instanceOf(Literal.class), this::eval),
                Case(instanceOf(Parenthesized.class), ex -> eval(scope, ex)),
                Case(instanceOf(Variable.class), ex -> eval(scope, ex)),
                Case(instanceOf(MapLiteral.class), ex -> eval(scope, ex)),
                Case(instanceOf(ListLiteral.class), ex -> eval(scope, ex)),
                Case(instanceOf(TupleLiteral.class), ex -> eval(scope, ex)),
                Case(instanceOf(Branch.class), ex -> eval(scope, ex)),
                Case(instanceOf(ModuleAccess.class), ex -> eval(scope, ex))
        );
    }
}
