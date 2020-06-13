package com.github.mhdirkse.countlang.ast;

import java.util.Arrays;
import java.util.List;

import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.ReturnHandler;

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
    public void execute(final ExecutionContext ctx, final ReturnHandler returnHandler) {
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
