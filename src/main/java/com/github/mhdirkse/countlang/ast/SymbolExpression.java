package com.github.mhdirkse.countlang.ast;

public final class SymbolExpression extends Expression {
    private Symbol symbol = null;

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(final Symbol symbol) {
        this.symbol = symbol;
    }

    @Override
    public Value calculate(final ExecutionContext ctx) {
        Scope scope = ctx.getScope();
        if (scope.hasSymbol(symbol.getName())) {
            return scope.getValue(symbol.getName());
        } else {
            throw new IllegalStateException("Undefined symbol " + symbol.getName());
        }
    }

    @Override
    public void accept(final AstNode.Visitor v) {
        v.visitSymbolExpression(this);
    }
}
