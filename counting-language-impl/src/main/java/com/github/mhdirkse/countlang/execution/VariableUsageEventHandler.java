package com.github.mhdirkse.countlang.execution;

public interface VariableUsageEventHandler {
    void variableNotUsed(String name, int line, int column);
}
