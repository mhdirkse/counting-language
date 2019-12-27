package com.github.mhdirkse.countlang.execution;

import java.util.HashMap;
import java.util.Map;

class StackFrame {
    private Map<String, Object> symbols;

    StackFrame() {
        symbols = new HashMap<String, Object>();
    }

    boolean hasSymbol(String name) {
        return symbols.containsKey(name);
    }

    Object getValue(String name) {
        return symbols.get(name);
    }

    void putSymbol(String name, Object value) {
        symbols.put(name, value);
    }
}
