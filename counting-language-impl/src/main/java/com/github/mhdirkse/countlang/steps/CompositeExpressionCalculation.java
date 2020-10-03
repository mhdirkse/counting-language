package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.CompositeExpression;

class CompositeExpressionCalculation extends CompositeExpressionHandler<Object> {
    CompositeExpressionCalculation(final CompositeExpression node) {
        super(node);
    }

    @Override
    public Object processSubExpressionResults(ExecutionContext<Object> context) {
        return node.getOperator().execute(subExpressionResults);        
    }
}
