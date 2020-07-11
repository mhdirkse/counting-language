package com.github.mhdirkse.countlang.execution;

import java.util.HashMap;
import java.util.Map;

import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;

public class FunctionDefinitions {
    private Map<String, FunctionDefinitionStatement> functions = new HashMap<>();

    public boolean hasFunction(final String name) {
        return functions.containsKey(name);
    }

    public FunctionDefinitionStatement getFunction(final String name) {
        return functions.get(name);
    }

    public void putFunction(final FunctionDefinitionStatement function) {
        functions.put(function.getName(), function);
    }
}
