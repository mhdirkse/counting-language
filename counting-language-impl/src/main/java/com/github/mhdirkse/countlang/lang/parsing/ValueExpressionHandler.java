package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.ProgramException;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.lang.CountlangLexer;

class ValueExpressionHandler extends AbstractCountlangListenerHandler implements ExpressionSource {
    private final int line;
    private final int column;
    private ValueExpression expression;

    @Override
    public ExpressionNode getExpression() {
        return expression;
    }

    ValueExpressionHandler(final int line, final int column) {
        super(false);
        this.line = line;
        this.column = column;
    }

    @Override
    public boolean visitTerminal(
            final TerminalNode node,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        if(node.getSymbol().getType() == CountlangLexer.BOOL) {
            expression = new ValueExpression(line, column, Boolean.valueOf(node.getText()));
            return true;
        } else if(node.getSymbol().getType() == CountlangLexer.INT) {
            try {
                expression = new ValueExpression(line, column, Integer.valueOf(node.getText()));
            } catch(NumberFormatException e) {
                throw new ProgramException(line, column, "Integer value is too big to store: " + node.getText());
            }
            return true;
        }
        return false;
    }
}
