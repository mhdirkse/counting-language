package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.SymbolExpression;

class SymbolExpressionTypeCheck extends AtomicHandler<CountlangType> {
    private final SymbolExpression node;

    SymbolExpressionTypeCheck(final SymbolExpression node) {
        this.node = node;
    }

    @Override
    public AstNode getAstNode() {
        return node;
    }

    @Override
    CountlangType getValue(ExecutionContext<CountlangType> context) {
        CountlangType symbolType = context.readSymbol(node.getSymbol());
        node.setCountlangType(symbolType);
        return symbolType;
    }
}
