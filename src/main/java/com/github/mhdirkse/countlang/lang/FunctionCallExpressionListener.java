package com.github.mhdirkse.countlang.lang;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.countlang.ast.Expression;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;

class FunctionCallExpressionListener extends AbstractChildExpressionListener {
    private final FunctionCallExpression expression;
    private final AbstractListener parent;

    FunctionCallExpressionListener(final int line, final int column, final AbstractListener parent) {
        expression = new FunctionCallExpression(line, column);
        this.parent = parent;
    }

    @Override
    void handleChildExpression(final Expression childExpression) {
        expression.addArgument(childExpression);
    }

    @Override
    public void visitTerminalImpl(final TerminalNode node) {
        if (node.getSymbol().getType() == CountlangParser.ID) {
            expression.setFunctionName(node.getText());
        }
    }

    @Override
    public void exitFunctionCallExpressionImpl(CountlangParser.FunctionCallExpressionContext ctx) {
        parent.visitFunctionCallExpression(expression);
        delegate = null;
    }
}
