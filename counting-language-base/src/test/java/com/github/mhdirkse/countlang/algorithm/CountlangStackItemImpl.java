package com.github.mhdirkse.countlang.algorithm;

class CountlangStackItemImpl implements CountlangStackItem {
    private final StackFrameAccess access;
    private final String symbol;

    CountlangStackItemImpl(StackFrameAccess access) {
        this.access = access;
        symbol = null;
    }

    CountlangStackItemImpl(StackFrameAccess access, String symbol) {
        this.access = access;
        this.symbol = symbol;
    }

    @Override
    public StackFrameAccess getAccess() {
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
