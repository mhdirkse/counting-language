package com.github.mhdirkse.countlang.ast;

import java.util.HashMap;
import java.util.Map;

public final class Scope {
    private Map<String, Value> symbols;

    public Scope() {
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
