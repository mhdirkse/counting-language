package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class FunctionCallExpressionHandler extends AbstractExpressionHandler
implements ExpressionSource, TerminalFilterCallback {
    private final TerminalFilter terminalFilter;
    private FunctionCallExpression expression;

    @Override
    public ExpressionNode getExpression() {
        return expression;
    }

    FunctionCallExpressionHandler(final int line, final int column) {
        expression = new FunctionCallExpression(line, column);
        terminalFilter = new TerminalFilter(this);
    }

    @Override
    public void addExpression(final ExpressionNode childExpression) {
        expression.addArgument(childExpression);
    }

    @Override
    public boolean visitTerminal(
            @NotNull final TerminalNode node,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return terminalFilter.visitTerminal(node, delegationCtx);
    }

    @Override
    public int getRequiredType() {
        return CountlangParser.ID;
    }

    @Override
    public void setText(final String text) {
        expression.setFunctionName(text);
    }
}
