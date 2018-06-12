package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.Expression;
import com.github.mhdirkse.countlang.ast.Value;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class ValueExpressionHandler2 extends AbstractTerminalHandler2 implements ExpressionSource {
    private ValueExpression expression;

    @Override
    public Expression getExpression() {
        return expression;
    }

    ValueExpressionHandler2(final int line, final int column) {
        expression = new ValueExpression(line, column);
    }

    @Override
    public int getRequiredType() {
        return CountlangParser.INT;
    }

    @Override
    public void setText(final String text) {
        expression.setValue(new Value(Integer.valueOf(text)));
    }
}