package com.github.mhdirkse.countlang.ast;

import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.Value;

public final class ValueExpression extends ExpressionNode {
    private Value value = null;

    public ValueExpression(final int line, final int column) {
        super(line, column);
    }

    public Value getValue() {
        return value;
    }

    public void setValue(final Value value) {
        this.value = value;
    }

    @Override
    public Value calculate(final ExecutionContext ctx) {
        return value;
    }

    @Override
    public void accept(final AstNode.Visitor v) {
        v.visitValueExpression(this);
    }
}
