package com.github.mhdirkse.countlang.execution;

public class SymbolFrameStackExecute extends SymbolFrameStackImpl<Object, SymbolFrame<Object>> {

    @Override
    SymbolFrame<Object> create(StackFrameAccess access) {
        return new SymbolFrameExecute(access);
    }
}
