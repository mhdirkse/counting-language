package com.github.mhdirkse.countlang.execution;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.ast.ArrayExpression;

final class ArrayExpressionCalculation extends ExpressionResultsCollector {
    ArrayExpressionCalculation(final ArrayExpression node) {
        super(node);
    }

    @Override
    void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext context) {
        context.onResult(new CountlangArray(new ArrayList<>(subExpressionResults)));
    }
}
