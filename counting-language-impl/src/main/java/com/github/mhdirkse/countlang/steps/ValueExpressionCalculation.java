package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.ValueExpression;

class ValueExpressionCalculation extends AtomicHandler<Object> {
    private ValueExpression node;

    ValueExpressionCalculation(final ValueExpression node) {
        super();
        this.node = node;
    }

    @Override
    public AstNode getAstNode() {
        return node;
    }

    @Override
    Object getValue(ExecutionContext<Object> context) {
        return node.getValue();
    }
}
