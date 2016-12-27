package com.github.mhdirkse.countlang.ast;

public final class SymbolExpression extends Expression {
    private Symbol symbol = null;

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(final Symbol symbol) {
        this.symbol = symbol;
    }
}
