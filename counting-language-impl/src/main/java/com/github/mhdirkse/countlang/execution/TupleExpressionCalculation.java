package com.github.mhdirkse.countlang.execution;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.ast.TupleExpression;

class TupleExpressionCalculation extends ExpressionResultsCollector {
    TupleExpressionCalculation(TupleExpression expr) {
        super(expr);
    }

    @Override
    void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext context) {
        context.onResult(new CountlangTuple(new ArrayList<>(subExpressionResults)));
    }
}
