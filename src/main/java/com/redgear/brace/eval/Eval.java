package com.redgear.brace.eval;

import com.redgear.brace.ast.*;
import com.redgear.brace.walk.Walker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;

/**
 * Created by LordBlackHole on 2016-12-30.
 */
public class Eval implements Walker {

    private static final Logger log = LoggerFactory.getLogger(Eval.class);
    private static Deque<Object> stack = new LinkedList<>();
    private LibraryScope libraryScope;
    private ModuleScope moduleScope;
    private LocalScope localScope;

    public Eval() {

        libraryScope = new LibraryScope();

        libraryScope.putFunc("+", args -> {

            if(args.size() != 2)
                throw new RuntimeException("Wrong number of arguments for op '+', found: " + args);

            Object left = args.get(0);
            Object right = args.get(1);

            if(left instanceof String || right instanceof String) {
                return String.valueOf(left) + String.valueOf(right);
            } else if (left instanceof Integer && right instanceof Integer) {
                return (Integer) left + (Integer) right;
            } else if(left instanceof Number && right instanceof Number) {
                return ((Number) left).doubleValue() + ((Number) right).doubleValue();
            } else {
                throw new RuntimeException("Illegal arguments to '+' op, found: " + args);
            }

        });
    }

    @Override
    public void walk(Assignment assignment) {
        String var = assignment.getVar().getName();

        walk(assignment.getExp());

        Object result = stack.pop();

        log.info("{} = {}", var, result);

        localScope.putValue(var, result);
    }

    @Override
    public void walk(Literal literal) {
        stack.push(literal.getValue());
    }

    @Override
    public void walk(Module module) {
        moduleScope = new ModuleScope(module.getName(), libraryScope);
        libraryScope.putModule(moduleScope);

        localScope = new LocalScope(moduleScope);

        module.getExpressions().forEach(this::walk);
    }

    @Override
    public void walk(Variable variable) {
        stack.push(localScope.getValue(variable.getName()));
    }

    @Override
    public void walk(Call call) {
        String module = call.getModuleRef().getName();

        @SuppressWarnings("unchecked")
        Function<List<Object>, Object> func = (Function<List<Object>, Object>) localScope.getValue(module, call.getMethod());

        if(func == null) {
            throw new RuntimeException("No such method: " + call.getMethod());
        } else {
            int size = call.getArguments().size();

            call.getArguments().forEach(this::walk);

            List<Object> args = new LinkedList<>();

            for(int i = 0; i < size; i++) {
                args.add(0, stack.pop());
            }

            stack.push(func.apply(args));
        }
    }

}
