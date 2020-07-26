package com.github.mhdirkse.countlang.execution;

import java.util.HashMap;
import java.util.Map;

import com.github.mhdirkse.countlang.ast.CountlangType;

class SymbolFrameTypeCheck implements SymbolFrame<CountlangType>{
    private final Map<String, CountlangType> symbols = new HashMap<>();

    private final SymbolNotAccessibleHandler handler;
    private final StackFrameAccess access;
    
    SymbolFrameTypeCheck(StackFrameAccess access, final SymbolNotAccessibleHandler handler) {
        this.handler = handler;
        this.access = access;
    }

    @Override
    public boolean has(String name) {
        return symbols.containsKey(name);
    }

    @Override
    public CountlangType read(String name, int line, int column) {
        if(!symbols.containsKey(name)) {
            handler.notReadable(name, line, column);
            return CountlangType.UNKNOWN;
        }
        return symbols.get(name);
    }

    @Override
    public void write(String name, CountlangType value, int line, int column) {
        if(symbols.containsKey(name)) {
            if(!symbols.get(name).equals(value)) {
                handler.notWritable(name, line, column);
            }
        }
        else {
            symbols.put(name, value);
        }
    }

    @Override
    public StackFrameAccess getAccess() {
        return access;
    }
}
