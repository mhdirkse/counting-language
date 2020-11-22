package com.github.mhdirkse.countlang.steps;

import java.util.List;

import com.github.mhdirkse.countlang.ast.ReturnStatement;

final class ReturnStatementCalculation extends ExpressionResultsCollector {
    ReturnStatementCalculation(ReturnStatement statement) {
        super(statement);
    }

    private ReturnStatementCalculation(final ReturnStatementCalculation orig) {
        super(orig);
    }

    @Override
    void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext context) {
        context.onResult(subExpressionResults.get(0));
    }

    @Override
    public AstNodeExecution fork() {
        return new ReturnStatementCalculation(this);
    }
}
