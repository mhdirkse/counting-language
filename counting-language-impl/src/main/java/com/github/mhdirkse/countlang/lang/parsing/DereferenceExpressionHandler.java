package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.DereferenceExpression;
import com.github.mhdirkse.countlang.ast.ExpressionNode;

public class DereferenceExpressionHandler extends AbstractExpressionHandler implements ExpressionSource {
    private boolean hasContainer = false;
    private DereferenceExpression result;

    DereferenceExpressionHandler(int line, int column) {
        result = new DereferenceExpression(line, column);
    }
    @Override
    void addExpression(ExpressionNode expression) {
        if(hasContainer) {
            result.setReference(expression);
        } else {
            result.setContainer(expression);
            hasContainer = true;
        }
    }

    @Override
    public ExpressionNode getExpression() {
        return result;
    }
}
