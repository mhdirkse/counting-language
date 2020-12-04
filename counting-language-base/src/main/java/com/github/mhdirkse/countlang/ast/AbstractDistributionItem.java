package com.github.mhdirkse.countlang.ast;

public abstract class AbstractDistributionItem extends AstNode implements CompositeNode {
    protected AbstractDistributionItem(int line, int column) {
        super(line, column);
    }

    public abstract ExpressionNode getItem();
    public abstract void setItem(ExpressionNode item);
}
