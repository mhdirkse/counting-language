package com.github.mhdirkse.countlang.lang;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.Value;
import com.github.mhdirkse.countlang.ast.ValueExpression;

public class ValueExpressionListener extends AbstractListener {
    private final ValueExpression expression;
    private final AstNode.Visitor parent;

    ValueExpressionListener(final int line, final int column, AstNode.Visitor parent) {
        expression = new ValueExpression(line, column);
        this.parent = parent;
    }

    @Override
    void visitTerminalImpl(final TerminalNode terminalNode) {
        if (terminalNode.getSymbol().getType() == CountlangParser.INT) {
            expression.setValue(new Value(Integer.valueOf(terminalNode.getText())));
        }
    }

    @Override
    public void exitValueExpressionImpl(final CountlangParser.ValueExpressionContext ctx) {
        parent.visitValueExpression(expression);
        delegate = null;
    }
}
