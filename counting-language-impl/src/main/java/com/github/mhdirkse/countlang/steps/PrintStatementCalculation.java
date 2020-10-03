package com.github.mhdirkse.countlang.steps;

import java.util.List;

import com.github.mhdirkse.countlang.ast.PrintStatement;

class PrintStatementCalculation extends ExpressionResultsCollector<Object> {
    PrintStatementCalculation(PrintStatement node) {
        super(node);
    }

    @Override
    void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext<Object> context) {
    }

    @Override
    boolean isDescendantResultHandled() {
        return true;
    }
}
