package com.github.mhdirkse.countlang.steps;

import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.ast.DistributionItemItem;

class DistributionItemItemCalculation extends ExpressionResultsCollector {
    private final Distribution.Builder builder;
    DistributionItemItemCalculation(DistributionItemItem node, Distribution.Builder builder) {
        super(node);
        this.builder = builder;
    }

    @Override
    void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext context) {
        builder.add((Integer) subExpressionResults.get(0));
    }
}
