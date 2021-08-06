package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.ArrayExpression;
import com.github.mhdirkse.countlang.ast.ExpressionNode;

public class ArrayExpressionHandler extends AbstractExpressionHandler implements ExpressionSource {
    private ArrayExpression result;

    public ArrayExpressionHandler(int line, int column) {
        result = new ArrayExpression(line, column);
    }

    @Override
    void addExpression(ExpressionNode expression) {
        result.addElement(expression);
    }

    @Override
    public ExpressionNode getExpression() {
        return result;
    }
}
