package com.github.mhdirkse.countlang.steps;

import java.util.List;

import com.github.mhdirkse.countlang.ast.CompositeExpression;

final class CompositeExpressionCalculation extends ExpressionResultsCollector {
    CompositeExpressionCalculation(final CompositeExpression node) {
        super(node);
    }

    @Override
    void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext context) {
        context.onResult(((CompositeExpression) node).getOperator().execute(subExpressionResults));     
    }
}
