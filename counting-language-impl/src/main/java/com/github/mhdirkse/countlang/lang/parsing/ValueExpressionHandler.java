package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.execution.ProgramException;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class ValueExpressionHandler extends AbstractTerminalHandler implements ExpressionSource {
    private final int line;
    private final int column;
    private ValueExpression expression;

    @Override
    public ExpressionNode getExpression() {
        return expression;
    }

    ValueExpressionHandler(final int line, final int column) {
        this.line = line;
        this.column = column;
    }

    @Override
    public int getRequiredType() {
        return CountlangParser.INT;
    }

    @Override
    public void setText(final String text) {
        try {
            Object value = Integer.valueOf(text);
            expression = new ValueExpression(line, column, value);
        } catch(final NumberFormatException e) {
            throw new ProgramException(line, column,
                    "Integer value is too big to store: " + text);
        }
    }
}
