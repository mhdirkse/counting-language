package com.github.mhdirkse.countlang.ast;

import java.util.HashMap;
import java.util.Map;

public final class Scope {
    private Map<String, Symbol> symbols;

    public Scope() {
        symbols = new HashMap<String, Symbol>();
    }

    public boolean hasSymbol(String name) {
        return symbols.containsKey(name);
    }

    public Symbol getSymbol(String name) {
        return symbols.get(name);
    }

    public Symbol addSymbol(String name) {
        Symbol symbol = new Symbol(name);
        symbols.put(name, symbol);
        return symbol;
    }
}
