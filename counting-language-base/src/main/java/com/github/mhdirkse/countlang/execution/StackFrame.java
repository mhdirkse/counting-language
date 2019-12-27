package com.github.mhdirkse.countlang.execution;

import java.util.HashMap;
import java.util.Map;

class StackFrame {
    private Map<String, Symbol> symbols;

    StackFrame() {
        symbols = new HashMap<String, Symbol>();
    }

    boolean hasSymbol(String name) {
        return symbols.containsKey(name);
    }

    Object getValue(String name) {
        return symbols.get(name).getValue();
    }

    CountlangType getCountlangType(String name) {
        return symbols.get(name).getCountlangType();
    }

    void putSymbol(String name, Object value) {
        Symbol symbol = new Symbol(name);
        symbol.setValue(value);
        symbols.put(name, symbol);
    }
}
