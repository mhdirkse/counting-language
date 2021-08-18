package com.github.mhdirkse.countlang.ast;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractTupleDealingLhsItem extends AstNode {
    @Getter @Setter
    private int variableNumber;

    public AbstractTupleDealingLhsItem(int line, int column) {
        super(line, column);
    }
}
