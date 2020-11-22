package com.github.mhdirkse.countlang.steps;

import java.util.List;

import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.types.Distribution;

final class PrintStatementCalculation extends ExpressionResultsCollector {
    PrintStatementCalculation(PrintStatement node) {
        super(node);
    }

    private PrintStatementCalculation(PrintStatementCalculation orig) {
        super(orig);
    }

    @Override
    void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext context) {
        Object value = subExpressionResults.get(0);
        String output = value.toString();
        if(value instanceof Distribution) {
            output = ((Distribution) value).format();
        }
        context.output(output);
    }

    @Override
    public AstNodeExecution fork() {
        return new PrintStatementCalculation(this);
    }
}
