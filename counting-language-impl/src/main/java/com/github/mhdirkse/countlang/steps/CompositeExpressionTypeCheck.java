package com.github.mhdirkse.countlang.steps;

import java.util.List;

import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.tasks.StatusCode;

class CompositeExpressionTypeCheck extends ExpressionResultsCollector<CountlangType> {
    CompositeExpressionTypeCheck(final CompositeExpression node) {
        super(node);
    }

    @Override
    void processSubExpressionResults(List<CountlangType> subExpressionResults, ExecutionContext<CountlangType> context) {
        CompositeExpression nodeAlias = (CompositeExpression) node;
        CountlangType expressionType = CountlangType.UNKNOWN;
        boolean typeEstablished = nodeAlias.getOperator().checkAndEstablishTypes(subExpressionResults);
        if(typeEstablished) {
            expressionType = nodeAlias.getOperator().getResultType();            
        } else {
            context.report(
                    StatusCode.OPERATOR_TYPE_MISMATCH,
                    nodeAlias.getLine(),
                    nodeAlias.getColumn(),
                    nodeAlias.getOperator().getName());
        }
        nodeAlias.setCountlangType(expressionType);
        context.onResult(expressionType);
    }

    @Override
    boolean isDescendantResultHandled() {
        return true;
    }
}
