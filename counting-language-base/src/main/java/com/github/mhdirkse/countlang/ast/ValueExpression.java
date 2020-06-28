package com.github.mhdirkse.countlang.ast;

public final class ValueExpression extends ExpressionNode {
    private final Object value;
    private final CountlangType countlangType;

    public ValueExpression(final int line, final int column, Object value) {
        super(line, column);
        this.value = value;
        countlangType = CountlangType.typeOf(value);
    }

    public Object getValue() {
        return value;
    }

    @Override
    public CountlangType getCountlangType() {
        return countlangType;
    }

    @Override
    public void accept(final Visitor v) {
        v.visitValueExpression(this);
    }
}
