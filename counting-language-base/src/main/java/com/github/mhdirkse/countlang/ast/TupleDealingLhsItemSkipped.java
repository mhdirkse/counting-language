package com.github.mhdirkse.countlang.ast;

public class TupleDealingLhsItemSkipped extends AbstractTupleDealingLhsItem {
    public TupleDealingLhsItemSkipped(int line, int column) {
        super(line, column);
    }

    @Override
    public void accept(Visitor v) {
        v.visitTupleDealingLhsItemSkipped(this);
    }
}
