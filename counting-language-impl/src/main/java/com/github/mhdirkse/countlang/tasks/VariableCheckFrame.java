package com.github.mhdirkse.countlang.tasks;

import com.github.mhdirkse.countlang.execution.StackFrameAccess;

interface VariableCheckFrame {
    void define(final String name, final int line, final int column);
    void use(final String name, final int line, final int column);
    void report(final StatusReporter reporter);
    boolean hasSymbol(String name);
    StackFrameAccess getStackFrameAccess();
}