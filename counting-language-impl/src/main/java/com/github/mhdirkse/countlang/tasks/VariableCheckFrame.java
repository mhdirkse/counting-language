package com.github.mhdirkse.countlang.tasks;

interface VariableCheckFrame {
    void define(final String name, final int line, final int column);
    void use(final String name, final int line, final int column);
    void report(final StatusReporter reporter);
}