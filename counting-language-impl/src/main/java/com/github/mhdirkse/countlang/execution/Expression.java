package com.github.mhdirkse.countlang.execution;

public interface Expression {
    Value calculate(final ExecutionContext ctx);
}
