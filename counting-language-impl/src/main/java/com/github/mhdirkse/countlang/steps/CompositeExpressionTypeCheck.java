package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.tasks.StatusCode;

class CompositeExpressionTypeCheck extends CompositeExpressionHandler<CountlangType> {
    CompositeExpressionTypeCheck(final CompositeExpression node) {
        super(node);
    }

    @Override
    CountlangType processSubExpressionResults(ExecutionContext<CountlangType> context) {
        CountlangType expressionType = CountlangType.UNKNOWN;
        boolean typeEstablished = node.getOperator().checkAndEstablishTypes(subExpressionResults);
        if(typeEstablished) {
            expressionType = node.getOperator().getResultType();            
        } else {
            context.report(
                    StatusCode.OPERATOR_TYPE_MISMATCH,
                    node.getLine(),
                    node.getColumn(),
                    node.getOperator().getName());
        }
        node.setCountlangType(expressionType);
        return expressionType;
    }
}
