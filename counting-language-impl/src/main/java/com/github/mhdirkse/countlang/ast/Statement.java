package com.github.mhdirkse.countlang.ast;

import com.github.mhdirkse.countlang.execution.ExecutionContext;

public abstract class Statement extends AstNode {
    public Statement(final int line, final int column) {
        super(line, column);
    }

    public abstract void execute(final ExecutionContext ctx);
}
