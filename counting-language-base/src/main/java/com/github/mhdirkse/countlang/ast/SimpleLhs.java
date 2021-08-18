package com.github.mhdirkse.countlang.ast;

import lombok.Getter;
import lombok.Setter;

public class SimpleLhs extends AbstractLhs {
    @Getter @Setter
    private String symbol;

    public SimpleLhs(int line, int column) {
        super(line, column);
    }

    @Override
    public void accept(Visitor v) {
        v.visitSimpleLhs(this);
    }
}
