package com.github.mhdirkse.countlang.execution;

public class SymbolFrameStackExecute extends SymbolFrameStackImpl<Object, SymbolFrame<Object>> {
    public SymbolFrameStackExecute() {
        super();
    }

    public SymbolFrameStackExecute(final SymbolFrameStackExecute orig) {
        super(orig);
    }

    @Override
    SymbolFrame<Object> create(StackFrameAccess access) {
        return new SymbolFrameExecute(access);
    }
}
