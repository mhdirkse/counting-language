package com.github.mhdirkse.countlang.ast;

public abstract class Expression {
    public abstract Value calculate(final ExecutionContext ctx);
}
