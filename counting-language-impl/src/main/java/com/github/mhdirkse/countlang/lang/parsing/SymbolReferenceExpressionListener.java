package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.Symbol;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class SymbolReferenceExpressionListener extends AbstractListener {
    private final SymbolExpression expression;
    private final AstNode.Visitor parent;

    SymbolReferenceExpressionListener(final int line, final int column, final AstNode.Visitor parent) {
        expression = new SymbolExpression(line, column);
        this.parent = parent;
    }

    @Override
    void visitTerminalImpl(final TerminalNode node) {
        if (node.getSymbol().getType() == CountlangParser.ID) {
            expression.setSymbol(new Symbol(node.getText()));
        }
    }

    @Override
    public void exitSymbolReferenceExpressionImpl(final CountlangParser.SymbolReferenceExpressionContext ctx) {
        parent.visitSymbolExpression(expression);
        delegate = null;
    }
}
