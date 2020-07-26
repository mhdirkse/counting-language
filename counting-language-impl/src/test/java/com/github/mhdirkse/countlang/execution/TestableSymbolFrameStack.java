package com.github.mhdirkse.countlang.execution;

import com.github.mhdirkse.countlang.utils.Stack;

class TestableSymbolFrameStack extends SymbolFrameStackImpl<DummyValue, SymbolFrameStub> {    
    TestableSymbolFrameStack(final Stack<SymbolFrameStub> arg) {
        super(arg);
    }

    @Override
    SymbolFrameStub create(StackFrameAccess access) {
        throw new IllegalStateException("TestableSymbolFrameStack does not support this method");
    }
}
