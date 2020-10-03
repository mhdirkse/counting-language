package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.ValueExpression;

class ValueExpressionTypeCheck extends AtomicHandler<CountlangType> {
    private final ValueExpression node;

    ValueExpressionTypeCheck(final ValueExpression node) {
        this.node = node;
    }

    @Override
    public AstNode getAstNode() {
        return node;
    }

    @Override
    CountlangType getValue(ExecutionContext<CountlangType> context) {
        return node.getCountlangType();
    }
}
