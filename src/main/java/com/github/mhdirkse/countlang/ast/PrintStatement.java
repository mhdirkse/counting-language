package com.github.mhdirkse.countlang.ast;

public final class PrintStatement extends Statement {
    private Expression expression = null;

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(final Expression expression) {
        this.expression = expression;
    }
}
