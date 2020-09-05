package com.github.mhdirkse.countlang.ast;

public class SimpleDistributionExpression extends AbstractDistributionExpression {
    public SimpleDistributionExpression(int line, int column) {
        super(line, column);
    }

    @Override
    public void accept(Visitor v) {
        v.visitSimpleDistributionExpression(this);
    }
}
