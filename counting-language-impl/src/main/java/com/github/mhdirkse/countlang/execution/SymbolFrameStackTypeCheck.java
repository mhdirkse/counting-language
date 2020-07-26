package com.github.mhdirkse.countlang.execution;

import com.github.mhdirkse.countlang.ast.CountlangType;

public class SymbolFrameStackTypeCheck extends SymbolFrameStackImpl<CountlangType, SymbolFrame<CountlangType>> {
    private SymbolNotAccessibleHandler handler;

    public SymbolFrameStackTypeCheck() {
    }

    public void setHandler(final SymbolNotAccessibleHandler handler) {
        this.handler = handler;
    }

    @Override
    SymbolFrame<CountlangType> create(StackFrameAccess access) {
        return new SymbolFrameTypeCheck(access, handler);
    }
}
