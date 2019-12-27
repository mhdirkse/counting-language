package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class SymbolReferenceExpressionHandler extends AbstractTerminalHandler implements ExpressionSource {
    private final int line;
    private final int column;
    private SymbolExpression expression;

    @Override
    public ExpressionNode getExpression() {
        return expression;
    }

    SymbolReferenceExpressionHandler(final int line, final int column) {
        this.line = line;
        this.column = column;
    }

    @Override
    public int getRequiredType() {
        return CountlangParser.ID;
    }

    @Override
    public void setText(final String text) {
        expression = new SymbolExpression(line, column, text);
    }
}
