package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class FunctionCallExpressionHandler2 extends AbstractExpressionHandler2
implements ExpressionSource, TerminalStrategyCallback2 {
    private final TerminalStrategy2 terminalStrategy;
    private FunctionCallExpression expression;

    @Override
    public ExpressionNode getExpression() {
        return expression;
    }

    FunctionCallExpressionHandler2(final int line, final int column) {
        expression = new FunctionCallExpression(line, column);
        terminalStrategy = new TerminalStrategy2(this);
    }

    @Override
    public void addExpression(final ExpressionNode childExpression) {
        expression.addArgument(childExpression);
    }

    @Override
    public boolean visitTerminal(
            @NotNull final TerminalNode node,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return terminalStrategy.visitTerminal(node, delegationCtx);
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
