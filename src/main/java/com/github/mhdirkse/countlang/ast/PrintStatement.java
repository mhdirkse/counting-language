package com.github.mhdirkse.countlang.ast;

public final class PrintStatement extends Statement {
    private Expression expression = null;

    public PrintStatement(final int line, final int column) {
        super(line, column);
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(final Expression expression) {
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
