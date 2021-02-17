package com.github.mhdirkse.countlang.algorithm;

class ScopeImpl implements Scope {
    private final ScopeAccess access;
    private final String symbol;

    ScopeImpl(ScopeAccess access) {
        this.access = access;
        symbol = null;
    }

    ScopeImpl(ScopeAccess access, String symbol) {
        this.access = access;
        this.symbol = symbol;
    }

    @Override
    public ScopeAccess getAccess() {
        return access;
    }

    @Override
    public boolean has(String testSymbol) {
        if(symbol == null) {
            return false;
        } else {
            return this.symbol.equals(testSymbol);
        }
    }
}
