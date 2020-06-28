package com.github.mhdirkse.countlang.ast;

public abstract class ExpressionNode extends AstNode {
    public ExpressionNode(final int line, final int column) {
        super(line, column);
    }

    public abstract CountlangType getCountlangType();
}
