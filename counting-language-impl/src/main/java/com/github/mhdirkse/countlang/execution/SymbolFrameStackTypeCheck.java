package com.github.mhdirkse.countlang.execution;

import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.StackFrameAccess;

public class SymbolFrameStackTypeCheck extends SymbolFrameStackImpl<CountlangType> {
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
