package com.github.mhdirkse.countlang.steps;

import java.util.List;

import com.github.mhdirkse.countlang.ast.AbstractDistributionExpression;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithTotal;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithUnknown;
import com.github.mhdirkse.countlang.ast.ProgramException;
import com.github.mhdirkse.countlang.types.Distribution;

abstract class SpecialDistributionExpressionCalculation extends ExpressionResultsCollector {
    SpecialDistributionExpressionCalculation(final AbstractDistributionExpression expression) {
        super(expression);
    }

    @Override
    final void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext context) {
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

    static final class WithTotal extends SpecialDistributionExpressionCalculation {
        WithTotal(DistributionExpressionWithTotal expression) {
            super(expression);
        }

        @Override
        int getUnknown(int total, int totalScored) {
            if(totalScored > total) {
                throw new ProgramException(
                        getAstNode().getLine(),
                        getAstNode().getColumn(),
                        String.format(
                                "The scored items in the distribution make count %d, which is more than %d",
                                totalScored, total));
            }
            return total - totalScored;
        }
    }

    static final class WithUnknown extends SpecialDistributionExpressionCalculation {
        WithUnknown(DistributionExpressionWithUnknown expression) {
            super(expression);
        }

        @Override
        int getUnknown(int unknown, int totalScored) {
            if(unknown < 0) {
                throw new ProgramException(
                        getAstNode().getLine(),
                        getAstNode().getColumn(),
                        String.format(
                                "The unknown count in a distribution cannot be negative; you tried %d",
                                unknown));
            }
            return unknown;
        }
    }
}
