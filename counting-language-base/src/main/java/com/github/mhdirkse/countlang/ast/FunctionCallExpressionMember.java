package com.github.mhdirkse.countlang.ast;

public class FunctionCallExpressionMember extends FunctionCallExpression {
    public FunctionCallExpressionMember(int line, int column) {
        super(line, column);
    }

    @Override
    public FunctionKey getKey() {
        return new FunctionKey(getName(), getArgument(0).getCountlangType());
    }

    @Override
    public void accept(Visitor v) {
        v.visitFunctionCallExpressionMember(this);
    }
}
