package com.github.mhdirkse.countlang.execution;

import java.util.HashMap;
import java.util.Map;

import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;

class FunctionDefinitions {
    private Map<String, FunctionDefinitionStatement> functions = new HashMap<>();

    boolean hasFunction(final String name) {
        return functions.containsKey(name);
    }

    FunctionDefinitionStatement getFunction(final String name) {
        return functions.get(name);
    }

    void putFunction(final FunctionDefinitionStatement function) {
        functions.put(function.getName(), function);
    }
}
