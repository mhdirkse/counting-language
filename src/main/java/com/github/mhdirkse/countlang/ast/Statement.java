package com.github.mhdirkse.countlang.ast;

public abstract class Statement extends AstNode {
    public Statement(final int line, final int column) {
        super(line, column);
    }

    public abstract void execute(final ExecutionContext ctx);
}
