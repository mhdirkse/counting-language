package com.github.mhdirkse.countlang.execution;

import java.util.HashMap;
import java.util.Map;

import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatementBase;

public class FunctionDefinitions {
    private Map<String, FunctionDefinitionStatementBase> functions = new HashMap<>();

    public boolean hasFunction(final String name) {
        return functions.containsKey(name);
    }

    public FunctionDefinitionStatementBase getFunction(final String name) {
        return functions.get(name);
    }

    public void putFunction(final FunctionDefinitionStatementBase function) {
        functions.put(function.getName(), function);
    }
}
