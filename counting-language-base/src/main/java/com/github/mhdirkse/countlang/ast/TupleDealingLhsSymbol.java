package com.github.mhdirkse.countlang.ast;

import lombok.Getter;
import lombok.Setter;

public class TupleDealingLhsSymbol extends AbstractTupleDealingLhsItem {
    @Getter @Setter
    private String symbol;

    public TupleDealingLhsSymbol(int line, int column) {
        super(line, column);
    }

    @Override
    public void accept(Visitor v) {
        v.visitTupleDealingLhsSymbol(this);
    }
}
