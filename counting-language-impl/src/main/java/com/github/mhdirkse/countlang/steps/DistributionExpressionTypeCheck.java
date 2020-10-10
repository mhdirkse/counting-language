package com.github.mhdirkse.countlang.steps;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.mhdirkse.countlang.ast.AbstractDistributionExpression;
import com.github.mhdirkse.countlang.ast.CountlangType;

class DistributionExpressionTypeCheck extends ExpressionResultsCollector<CountlangType> {
    DistributionExpressionTypeCheck(final AbstractDistributionExpression expression) {
        super(expression);
    }

    @Override
    void processSubExpressionResults(List<CountlangType> subExpressionResults,
            ExecutionContext<CountlangType> context) {
        Set<CountlangType> uniqueTypes = new HashSet<>(subExpressionResults);
        if(uniqueTypes.size() != 1) {
            // TODO: Report that not all sub expresions have the same type.
        } else if(!uniqueTypes.iterator().next().equals(CountlangType.INT)) {
            // TODO: Report that the unique sub expression is not of type INT.
        } else {
            context.onResult(CountlangType.DISTRIBUTION);
        }
    }

    @Override
    boolean isDescendantResultHandled() {
        return true;
    }
}
