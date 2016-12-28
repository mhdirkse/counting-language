package com.github.mhdirkse.countlang.ast;

public final class ValueExpression extends Expression {
    private Value value = null;

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
}
