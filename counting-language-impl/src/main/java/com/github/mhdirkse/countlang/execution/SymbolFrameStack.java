package com.github.mhdirkse.countlang.execution;

import com.github.mhdirkse.countlang.ast.StackFrameAccess;

public interface SymbolFrameStack<T> {
    void pushFrame(StackFrameAccess stackFrameAccess);
    void popFrame();
    T read(String name, int line, int column);
    <V extends T> void write(String name, V value, int line, int column);
}
