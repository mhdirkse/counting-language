package com.github.mhdirkse.countlang.ast;

import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.ProgramException;
import com.github.mhdirkse.countlang.execution.Value;

public final class SymbolExpression extends ExpressionNode {
    private String symbol = null;

    public SymbolExpression(final int line, final int column) {
        super(line, column);
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(final String symbol) {
        this.symbol = symbol;
    }

    @Override
    public Value calculate(final ExecutionContext ctx) {
        if (ctx.hasSymbol(symbol)) {
            return ctx.getValue(symbol);
        } else {
            throw new ProgramException(getLine(), getColumn(), "Undefined symbol " + symbol);
        }
    }

    @Override
    public void accept(final Visitor v) {
        v.visitSymbolExpression(this);
    }
}
