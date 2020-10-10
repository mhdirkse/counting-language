package com.github.mhdirkse.countlang.steps;

import java.util.List;

import com.github.mhdirkse.countlang.ast.AssignmentStatement;

class AssignmentStatementHandler<T> extends ExpressionResultsCollector<T> {
    final private String symbol;

    AssignmentStatementHandler(final AssignmentStatement statement) {
        super(statement);
        symbol = statement.getLhs();
    }

    @Override
    void processSubExpressionResults(List<T> subExpressionResults, ExecutionContext<T> context) {
        context.writeSymbol(symbol, subExpressionResults.get(0));
    }

    @Override
    boolean isDescendantResultHandled() {
        return true;
    }
}
