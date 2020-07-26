package com.github.mhdirkse.countlang.execution;

public interface SymbolFrameStack<T> {
    void pushFrame(StackFrameAccess stackFrameAccess);
    void popFrame();
    T read(String name, int line, int column);
    void write(String name, T value, int line, int column);
}
