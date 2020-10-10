package com.github.mhdirkse.countlang.steps;

import java.util.List;

import com.github.mhdirkse.countlang.ast.AbstractDistributionExpression;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithTotal;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithUnknown;
import com.github.mhdirkse.countlang.types.Distribution;

abstract class SpecialDistributionExpressionCalculation extends ExpressionResultsCollector<Object> {
    SpecialDistributionExpressionCalculation(final AbstractDistributionExpression expression) {
        super(expression);
    }

    @Override
    void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext<Object> context) {
        int extraSubExpressionResult = ((Integer) subExpressionResults.get(0)).intValue();
        Distribution.Builder builder = new Distribution.Builder();
        for(Object scored: subExpressionResults.subList(1, subExpressionResults.size())) {
            builder.add(((Integer) scored).intValue());
        }
        int totalScored = builder.getTotal();
        builder.addUnknown(getUnknown(extraSubExpressionResult, totalScored));
        context.onResult(builder.build());
    }

    abstract int getUnknown(int extraSubExpressionResult, int totalScored);

    @Override
    boolean isDescendantResultHandled() {
        return true;
    }

    static class WithTotal extends SpecialDistributionExpressionCalculation {
        WithTotal(DistributionExpressionWithTotal expression) {
            super(expression);
        }

        @Override
        int getUnknown(int total, int totalScored) {
            if(totalScored > total) {
                // TODO: Throw exception because the total is too low
            }
            return total - totalScored;
        }
    }

    static class WithUnknown extends SpecialDistributionExpressionCalculation {
        WithUnknown(DistributionExpressionWithUnknown expression) {
            super(expression);
        }

        @Override
        int getUnknown(int unknown, int totalScored) {
            return unknown;
        }
    }
}
