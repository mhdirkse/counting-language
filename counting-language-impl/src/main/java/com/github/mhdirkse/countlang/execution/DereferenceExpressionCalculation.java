package com.github.mhdirkse.countlang.execution;

import java.math.BigInteger;
import java.util.List;

import com.github.mhdirkse.countlang.ast.DereferenceExpression;
import com.github.mhdirkse.countlang.ast.ProgramException;

final class DereferenceExpressionCalculation extends ExpressionResultsCollector {
    DereferenceExpressionCalculation(final DereferenceExpression node) {
        super(node);
    }

    @Override
    final void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext context) {
        CountlangArray container = (CountlangArray) subExpressionResults.get(0);
        BigInteger index = (BigInteger) subExpressionResults.get(1);
        if(index.compareTo(BigInteger.ONE) < 0) {
            throw new ProgramException(getAstNode().getLine(), getAstNode().getColumn(), String.format("Invalid array index %s", index.toString()));
        }
        if(index.compareTo(BigInteger.valueOf((long) container.size())) > 0) {
            throw new ProgramException(getAstNode().getLine(), getAstNode().getColumn(), String.format("Array index %s more than array size %d", index.toString(), subExpressionResults.size()));
        }
        int indexToExtract = (int) index.longValue() - 1;
        context.onResult(container.get(indexToExtract));
    }
}
