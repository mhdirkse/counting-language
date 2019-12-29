package com.github.mhdirkse.countlang.ast;

import java.util.Arrays;
import java.util.List;

import com.github.mhdirkse.countlang.execution.ExecutionContext;

public final class PrintStatement extends Statement implements CompositeNode {
    private ExpressionNode expression = null;

    public PrintStatement(final int line, final int column) {
        super(line, column);
    }

    public ExpressionNode getExpression() {
        return expression;
    }

    public void setExpression(final ExpressionNode expression) {
        this.expression = expression;
    }

    @Override
    public void execute(final ExecutionContext ctx) {
        String result = expression.calculate(ctx).toString();
        ctx.output(result);
    }

    @Override
    public void accept(final Visitor v) {
        v.visitPrintStatement(this);
    }

    @Override
    public List<AstNode> getChildren() {
        return Arrays.asList(expression);
    }
}
