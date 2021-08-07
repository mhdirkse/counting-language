package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.TupleExpression;

class TupleExpressionHandler extends AbstractExpressionHandler implements ExpressionSource {
    private TupleExpression result;

    TupleExpressionHandler(int line, int column) {
        result = new TupleExpression(line, column);
    }

    @Override
    void addExpression(ExpressionNode expression) {
        result.addChild(expression);
    }

    @Override
    public ExpressionNode getExpression() {
        return result;
    }
}
