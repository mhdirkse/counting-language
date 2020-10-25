package com.github.mhdirkse.countlang.steps;

import java.util.List;

import com.github.mhdirkse.countlang.ast.CompositeExpression;

class CompositeExpressionCalculation extends ExpressionResultsCollector {
    CompositeExpressionCalculation(final CompositeExpression node) {
        super(node);
    }

    @Override
    public void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext context) {
        context.onResult(((CompositeExpression) node).getOperator().execute(subExpressionResults));     
    }

    @Override
    boolean isDescendantResultHandled() {
        return true;
    }
}
