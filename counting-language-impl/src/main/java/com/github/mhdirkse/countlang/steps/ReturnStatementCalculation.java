package com.github.mhdirkse.countlang.steps;

import java.util.List;

import com.github.mhdirkse.countlang.ast.ReturnStatement;

final class ReturnStatementCalculation extends ExpressionResultsCollector {
    ReturnStatementCalculation(ReturnStatement statement) {
        super(statement);
    }

    @Override
    void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext context) {
        context.onResult(subExpressionResults.get(0));
    }
}
