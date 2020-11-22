package com.github.mhdirkse.countlang.steps;

import java.util.List;

import com.github.mhdirkse.countlang.ast.MarkUsedStatement;

final class MarkUsedStatementCalculation extends ExpressionResultsCollector {
    MarkUsedStatementCalculation(MarkUsedStatement node) {
        super(node);
    }

    private MarkUsedStatementCalculation(MarkUsedStatementCalculation orig) {
        super(orig);
    }

    @Override
    void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext context) {
    }

    @Override
    public AstNodeExecution fork() {
        return new MarkUsedStatementCalculation(this);
    }
}
