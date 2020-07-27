package com.github.mhdirkse.countlang.execution;

import java.util.HashMap;
import java.util.Map;

import com.github.mhdirkse.countlang.ast.CountlangType;

/**
 * Implements a {@link SymbolFrame} for two purposes. First, checking
 * whether variables exist. Second, checking that each write to
 * a variable is done with the same {@link com.github.mhdirkse.countlang.ast.CountlangType}.
 * 
 * Every branch of an if-statement opens a new block scope. Therefore, we assume that
 * no new symbols are added while any switch statement is opened. Within a branch,
 * the same symbols exist in this frame as existed before the switch statement
 * was started. Therefore we do not have to distinguish branches to check
 * whether a symbol exists.
 * 
 * @author martijn
 *
 */
class SymbolFrameTypeCheck implements SymbolFrame<CountlangType>{
    private final Map<String, CountlangType> symbols = new HashMap<>();

    private final SymbolNotAccessibleHandler handler;
    private final StackFrameAccess access;
    
    private int numSwitchOpen = 0;
    
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
        else if(numSwitchOpen >= 1) {
            throw new IllegalStateException("Expected no new symbol within a switch statement: " + name);
        }
        else {
            symbols.put(name, value);
        }
    }

    @Override
    public StackFrameAccess getAccess() {
        return access;
    }

    @Override
    public void onSwitchOpened() {
        numSwitchOpen++;
    }

    @Override
    public void onSwitchClosed() {
        numSwitchOpen--;
    }

    @Override
    public void onBranchOpened() {
    }

    @Override
    public void onBranchClosed() {
    }
}
