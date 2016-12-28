package com.github.mhdirkse.countlang.ast;

public abstract class Statement {
    public abstract void execute(final ExecutionContext ctx);
}
