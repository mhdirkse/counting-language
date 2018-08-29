package com.github.mhdirkse.countlang.ast;

import com.github.mhdirkse.countlang.execution.ExecutionContext;

public final class PrintStatement extends Statement {
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
        String result = Integer.toString(expression.calculate(ctx).getValue());
        ctx.getOutputStrategy().output(result);
    }

    @Override
    public void accept(final AstNode.Visitor v) {
        v.visitPrintStatement(this);
    }
}
