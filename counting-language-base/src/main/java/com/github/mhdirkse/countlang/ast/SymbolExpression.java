package com.github.mhdirkse.countlang.ast;

import com.github.mhdirkse.countlang.execution.CountlangType;
import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.ProgramException;

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
    public Object calculate(final ExecutionContext ctx) {
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
