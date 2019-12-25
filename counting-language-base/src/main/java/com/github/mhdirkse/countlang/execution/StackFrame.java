package com.github.mhdirkse.countlang.execution;

import java.util.HashMap;
import java.util.Map;

class StackFrame {
    private Map<String, Value> symbols;

    StackFrame() {
        symbols = new HashMap<String, Value>();
    }

    boolean hasSymbol(String name) {
        return symbols.containsKey(name);
    }

    Value getValue(String name) {
        return symbols.get(name);
    }

    void putSymbol(String name, Value value) {
        symbols.put(name, value);
    }
}
