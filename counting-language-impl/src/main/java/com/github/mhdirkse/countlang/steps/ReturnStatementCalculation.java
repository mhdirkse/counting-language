package com.github.mhdirkse.countlang.steps;

import java.util.List;

import com.github.mhdirkse.countlang.ast.ReturnStatement;

class ReturnStatementCalculation extends ExpressionResultsCollector {
    ReturnStatementCalculation(ReturnStatement statement) {
        super(statement);
    }

    @Override
    boolean isDescendantResultHandled() {
        return false;
    }

    @Override
    void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext context) {
    }
}
