package com.github.mhdirkse.countlang.ast;

public abstract class Statement extends AstNode {
    public abstract void execute(final ExecutionContext ctx);
}
