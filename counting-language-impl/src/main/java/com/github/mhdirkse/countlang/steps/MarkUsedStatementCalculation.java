package com.github.mhdirkse.countlang.steps;

import java.util.List;

import com.github.mhdirkse.countlang.ast.MarkUsedStatement;

class MarkUsedStatementCalculation extends ExpressionResultsCollector {
    MarkUsedStatementCalculation(MarkUsedStatement node) {
        super(node);
    }

    @Override
    void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext context) {
    }

    @Override
    boolean isDescendantResultHandled() {
        return true;
    }
}
