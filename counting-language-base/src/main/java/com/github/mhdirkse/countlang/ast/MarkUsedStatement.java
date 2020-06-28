package com.github.mhdirkse.countlang.ast;

import java.util.Arrays;
import java.util.List;

public final class MarkUsedStatement extends Statement implements CompositeNode {
    private ExpressionNode expression = null;

    public MarkUsedStatement(final int line, final int column) {
        super(line, column);
    }

    public ExpressionNode getExpression() {
        return expression;
    }

    public void setExpression(final ExpressionNode expression) {
        this.expression = expression;
    }

    @Override
    public void accept(final Visitor v) {
        v.visitMarkUsedStatement(this);
    }

    @Override
    public List<AstNode> getChildren() {
        return Arrays.asList(expression);
    }
}
