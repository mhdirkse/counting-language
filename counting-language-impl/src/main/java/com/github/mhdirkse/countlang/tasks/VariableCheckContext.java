package com.github.mhdirkse.countlang.tasks;

interface VariableCheckContext {
    void define(final String name, final int line, final int column);
    void use(final String name, final int line, final int column);
    void pushNewFrame();
    void popFrame();
    void report(final StatusReporter reporter);
}
