package com.github.mhdirkse.countlang.steps;

import java.util.List;

import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.types.Distribution;

class MarkUsedCalculation extends ExpressionResultsCollector<Object> {
    MarkUsedCalculation(PrintStatement node) {
        super(node);
    }

    @Override
    void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext<Object> context) {
        Object value = subExpressionResults.get(0);
        String output = value.toString();
        if(value instanceof Distribution) {
            output = ((Distribution) value).format();
        }
        context.output(output);
    }

    @Override
    boolean isDescendantResultHandled() {
        return true;
    }
}
