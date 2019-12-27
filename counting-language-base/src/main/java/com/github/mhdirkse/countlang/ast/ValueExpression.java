package com.github.mhdirkse.countlang.ast;

import com.github.mhdirkse.countlang.execution.ExecutionContext;

public final class ValueExpression extends ExpressionNode {
    private Object value = null;

    public ValueExpression(final int line, final int column) {
        super(line, column);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(final Object value) {
        this.value = value;
    }

    @Override
    public Object calculate(final ExecutionContext ctx) {
        return value;
    }

    @Override
    public void accept(final Visitor v) {
        v.visitValueExpression(this);
    }
}
