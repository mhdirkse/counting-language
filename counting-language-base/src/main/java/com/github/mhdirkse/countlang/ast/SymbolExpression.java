package com.github.mhdirkse.countlang.ast;

import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.ProgramException;
import com.github.mhdirkse.countlang.execution.Symbol;
import com.github.mhdirkse.countlang.execution.Value;

public final class SymbolExpression extends ExpressionNode {
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
            throw new ProgramException(getLine(), getColumn(), "Undefined symbol " + symbol.getName());
        }
    }

    @Override
    public void accept(final Visitor v) {
        v.visitSymbolExpression(this);
    }
}
