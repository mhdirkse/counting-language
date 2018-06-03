package com.github.mhdirkse.countlang.ast;

import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.ProgramRuntimeException;

public final class SymbolExpression extends Expression {
    private Symbol symbol = null;

    public SymbolExpression(final int line, final int column) {
        super(line, column);
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(final Symbol symbol) {
        this.symbol = symbol;
    }

    @Override
    public Value calculate(final ExecutionContext ctx) {
        if (ctx.hasSymbol(symbol.getName())) {
            return ctx.getValue(symbol.getName());
        } else {
            throw new ProgramRuntimeException(getLine(), getColumn(), "Undefined symbol " + symbol.getName());
        }
    }

    @Override
    public void accept(final AstNode.Visitor v) {
        v.visitSymbolExpression(this);
    }
}
