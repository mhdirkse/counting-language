package com.github.mhdirkse.countlang.execution;

import com.github.mhdirkse.countlang.ast.StackFrameAccess;

interface SymbolFrame<T> {
    boolean has(String name);
    T read(String name, int line, int column);
    <V extends T> void write(String name, V value, int line, int column);
    StackFrameAccess getAccess();
}
