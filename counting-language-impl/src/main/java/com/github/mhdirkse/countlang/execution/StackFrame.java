package com.github.mhdirkse.countlang.execution;

import java.util.HashMap;
import java.util.Map;

import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.StackFrameAccess;

import lombok.Getter;

class StackFrame {
    @Getter
    private final StackFrameAccess stackFrameAccess;

    private Map<String, Symbol> symbols;

    StackFrame(final StackFrameAccess stackFrameAccess) {
        this.stackFrameAccess = stackFrameAccess;
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
