package com.github.mhdirkse.countlang.ast;

import lombok.Getter;
import lombok.Setter;

public final class SymbolExpression extends ExpressionNode {
    private final String symbol;

    @Getter
    @Setter
    private CountlangType countlangType = CountlangType.UNKNOWN;

    public SymbolExpression(final int line, final int column, String symbol) {
        super(line, column);
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public void accept(final Visitor v) {
        v.visitSymbolExpression(this);
    }
}
