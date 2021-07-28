package com.github.mhdirkse.countlang.ast;

import lombok.Setter;

public class FunctionCallExpressionNonMember extends FunctionCallExpression {
    @Setter
    private String name;

    public FunctionCallExpressionNonMember(final int line, final int column) {
        super(line, column);
    }

    @Override
    public FunctionKey getKey() {
        return new FunctionKey(name);
    }

    @Override
    public void accept(final Visitor v) {
        v.visitFunctionCallExpressionNonMember(this);
    }
}
