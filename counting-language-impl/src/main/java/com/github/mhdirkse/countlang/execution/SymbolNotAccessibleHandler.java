package com.github.mhdirkse.countlang.execution;

public interface SymbolNotAccessibleHandler {
    void notReadable(String name, int line, int column);
    void notWritable(String name, int line, int column);
}
