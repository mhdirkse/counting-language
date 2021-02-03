package com.github.mhdirkse.countlang.steps;

import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.ast.DistributionItemCount;
import com.github.mhdirkse.countlang.ast.ProgramException;

class DistributionItemCountCalculation extends ExpressionResultsCollector {
    private final Distribution.Builder builder;

    DistributionItemCountCalculation(DistributionItemCount node, Distribution.Builder builder) {
        super(node);
        this.builder = builder;
    }

    @Override
    void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext context) {
        int count = (Integer) subExpressionResults.get(0);
        int item = (Integer) subExpressionResults.get(1);
        if(count < 0) {
            throw new ProgramException(getAstNode().getLine(), getAstNode().getColumn(),
                    String.format("Item is added to distribution with negative count %d", count));
        }
        builder.add(item, count);
    }
}
