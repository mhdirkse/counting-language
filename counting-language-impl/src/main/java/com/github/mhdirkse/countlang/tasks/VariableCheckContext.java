package com.github.mhdirkse.countlang.tasks;

import com.github.mhdirkse.countlang.ast.StackFrameAccess;

interface VariableCheckContext {
    void define(final String name, final int line, final int column);
    void use(final String name, final int line, final int column);
    void pushNewFrame(StackFrameAccess stackFrameAccess);
    void popFrame();
    void report(final StatusReporter reporter);
}
