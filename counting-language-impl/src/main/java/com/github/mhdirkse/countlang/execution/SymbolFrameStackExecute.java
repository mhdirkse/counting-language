package com.github.mhdirkse.countlang.execution;

import com.github.mhdirkse.countlang.ast.StackFrameAccess;

public class SymbolFrameStackExecute extends SymbolFrameStackImpl<Object> {

    @Override
    SymbolFrame<Object> create(StackFrameAccess access) {
        return new SymbolFrameExecute(access);
    }
}
