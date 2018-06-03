package com.github.mhdirkse.countlang.execution;

import java.util.HashMap;
import java.util.Map;

import com.github.mhdirkse.countlang.ast.RunnableFunction;

class FunctionDefinitions {
    private Map<String, RunnableFunction> functions = new HashMap<>();

    boolean hasFunction(final String name) {
        return functions.containsKey(name);
    }

    RunnableFunction getFunction(final String name) {
        return functions.get(name);
    }

    void putFunction(final RunnableFunction function) {
        functions.put(function.getName(), function);
    }
}
