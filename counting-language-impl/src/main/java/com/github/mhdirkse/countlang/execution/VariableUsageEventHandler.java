package com.github.mhdirkse.countlang.execution;

@FunctionalInterface
public interface VariableUsageEventHandler {
    void variableNotUsed(String name, int line, int column);
}
