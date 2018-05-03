package com.github.mhdirkse.countlang.ast;

public abstract class Expression extends AstNode {
    public Expression(final int line, final int column) {
        super(line, column);
    }

    public abstract Value calculate(final ExecutionContext ctx);
}
