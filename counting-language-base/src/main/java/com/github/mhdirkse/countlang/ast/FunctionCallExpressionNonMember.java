package com.github.mhdirkse.countlang.ast;

public class FunctionCallExpressionNonMember extends FunctionCallExpression {
    public FunctionCallExpressionNonMember(final int line, final int column) {
        super(line, column);
    }

    @Override
    public FunctionKey getKey() {
        return new FunctionKey(getName());
    }

    @Override
    public void accept(final Visitor v) {
        v.visitFunctionCallExpressionNonMember(this);
    }
}
