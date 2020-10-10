package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.execution.DummyValue;

class ValueExpressionVarUsage extends AtomicHandler<DummyValue> {
    private final ValueExpression expression;

    ValueExpressionVarUsage(final ValueExpression expression) {
        this.expression = expression;
    }

    @Override
    public AstNode getAstNode() {
        return expression;
    }

    @Override
    DummyValue getValue(ExecutionContext<DummyValue> context) {
        context.onResult(DummyValue.getInstance());
        return DummyValue.getInstance();
    }
}
