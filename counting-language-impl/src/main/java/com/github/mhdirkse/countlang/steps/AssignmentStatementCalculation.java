package com.github.mhdirkse.countlang.steps;

import java.util.List;

import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.AstNode;

final class AssignmentStatementCalculation extends ExpressionResultsCollector {
    final private String symbol;

    AssignmentStatementCalculation(final AssignmentStatement statement) {
        super(statement);
        symbol = statement.getLhs();
    }

    @Override
    void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext context) {
        context.writeSymbol(symbol, subExpressionResults.get(0), (AstNode) node);
    }
}
