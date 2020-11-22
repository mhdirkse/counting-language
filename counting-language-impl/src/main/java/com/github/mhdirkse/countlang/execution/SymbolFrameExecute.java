package com.github.mhdirkse.countlang.execution;

import java.util.HashMap;
import java.util.Map;

import com.github.mhdirkse.countlang.ast.ProgramException;

class SymbolFrameExecute implements SymbolFrame<Object> {
    private final Map<String, Object> symbols;

    private final StackFrameAccess access;

    public SymbolFrameExecute(StackFrameAccess access) {
        symbols = new HashMap<>();
        this.access = access;
    }

    SymbolFrameExecute(SymbolFrameExecute orig) {
        Map<String, Object> theSymbols = new HashMap<>();
        theSymbols.putAll(orig.symbols);
        this.symbols = theSymbols;
        this.access = orig.access;
    }

    @Override
    public StackFrameAccess getAccess() {
        return access;
    }

    @Override
    public boolean has(String name) {
        return symbols.containsKey(name);
    }

    @Override
    public Object read(String name, int line, int column) {
        if(symbols.containsKey(name)) {
            return symbols.get(name);
        }
        throw new ProgramException(line, column, "Undefined variable %s" + name);
    }

    @Override
    public void write(String name, Object value, int line, int column) {
        symbols.put(name, value);
    }

    @Override
    public void onSwitchOpened() {
    }

    @Override
    public void onSwitchClosed() {
    }

    @Override
    public void onBranchOpened() {
    }

    @Override
    public void onBranchClosed() {
    }
}
