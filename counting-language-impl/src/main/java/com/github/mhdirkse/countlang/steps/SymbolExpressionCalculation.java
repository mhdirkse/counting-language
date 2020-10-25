package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.SymbolExpression;

class SymbolExpressionCalculation extends AtomicCalculation {
    private final SymbolExpression node;

    SymbolExpressionCalculation(final SymbolExpression node) {
        super();
        this.node = node;
    }

    @Override
    public AstNode getAstNode() {
        return node;
    }

    @Override
    Object getValue(final ExecutionContext context) {
        return context.readSymbol(node.getSymbol(), node);
    }
}
