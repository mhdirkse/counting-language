package com.github.mhdirkse.countlang.execution;

interface SymbolFrame<T> {
    boolean has(String name);
    T read(String name, int line, int column);
    void write(String name, T value, int line, int column);
    StackFrameAccess getAccess();
}
