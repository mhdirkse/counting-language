package com.github.mhdirkse.countlang.ast;

public class EmptyCollectionExpression extends ExpressionNode {
    private CountlangType countlangType;

    public EmptyCollectionExpression(int line, int column, CountlangType countlangType) {
        super(line, column);
        this.countlangType = countlangType;
    }

    @Override
    public CountlangType getCountlangType() {
        return countlangType;
    }

    @Override
    public void accept(Visitor v) {
        v.visitEmptyCollectionExpression(this);
    }
}
