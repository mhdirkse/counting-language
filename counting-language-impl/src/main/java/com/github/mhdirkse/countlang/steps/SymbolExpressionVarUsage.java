package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.execution.DummyValue;

class SymbolExpressionVarUsage extends AtomicHandler<DummyValue> {
    private final SymbolExpression expression;

    SymbolExpressionVarUsage(final SymbolExpression expression) {
        this.expression = expression;
    }

    @Override
    public AstNode getAstNode() {
        return expression;
    }

    @Override
    DummyValue getValue(ExecutionContext<DummyValue> context) {
        context.readSymbol(expression.getSymbol());
        return DummyValue.getInstance();
    }
}
