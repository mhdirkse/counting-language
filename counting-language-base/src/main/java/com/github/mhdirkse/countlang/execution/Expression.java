package com.github.mhdirkse.countlang.execution;

public interface Expression {
    Object calculate(final ExecutionContext ctx);
}
