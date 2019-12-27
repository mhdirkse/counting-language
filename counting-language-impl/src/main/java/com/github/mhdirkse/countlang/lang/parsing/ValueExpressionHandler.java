package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.execution.ProgramException;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class ValueExpressionHandler extends AbstractTerminalHandler implements ExpressionSource {
    private ValueExpression expression;

    @Override
    public ExpressionNode getExpression() {
        return expression;
    }

    ValueExpressionHandler(final int line, final int column) {
        expression = new ValueExpression(line, column);
    }

    @Override
    public int getRequiredType() {
        return CountlangParser.INT;
    }

    @Override
    public void setText(final String text) {
        try {
            expression.setValue(Integer.valueOf(text));
        } catch(final NumberFormatException e) {
            throw new ProgramException(expression.getLine(), expression.getColumn(),
                    "Integer value is too big to store: " + text);
        }
    }
}