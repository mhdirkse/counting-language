package com.github.mhdirkse.countlang.steps;

import java.util.List;

import com.github.mhdirkse.countlang.ast.SimpleDistributionExpression;
import com.github.mhdirkse.countlang.types.Distribution;

class SimpleDistributionExpressionCalculation extends ExpressionResultsCollector {
    SimpleDistributionExpressionCalculation(SimpleDistributionExpression expression) {
        super(expression);
    }

    @Override
    void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext context) {
        Distribution.Builder builder = new Distribution.Builder();
        for(Object subExpressionResult: subExpressionResults) {
            builder.add(((Integer) subExpressionResult).intValue());
        }
        context.onResult(builder.build());
    }

    @Override
    boolean isDescendantResultHandled() {
        return true;
    }
}
