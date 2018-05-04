package com.github.mhdirkse.countlang.execution;

import java.util.HashMap;
import java.util.Map;

import com.github.mhdirkse.countlang.ast.Value;

public class StackFrame {
    private Map<String, Value> symbols;

    public StackFrame() {
        symbols = new HashMap<String, Value>();
    }

    public boolean hasSymbol(String name) {
        return symbols.containsKey(name);
    }

    public Value getValue(String name) {
        return symbols.get(name);
    }

    public void putSymbol(String name, Value value) {
        symbols.put(name, value);
    }
}
