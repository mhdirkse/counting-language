package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.SampleStatement;
import com.github.mhdirkse.countlang.ast.Statement;
import com.github.mhdirkse.countlang.lang.CountlangLexer;

class SampleStatementHandler extends AbstractExpressionHandler
implements StatementSource, TerminalFilterCallback {
    private final TerminalFilter terminalFilter;
    private SampleStatement statement;

    @Override
    public Statement getStatement() {
        return statement;
    }

    SampleStatementHandler(final int line, final int column) {
        statement = new SampleStatement(line, column);
        terminalFilter = new TerminalFilter(this);
    }

    @Override
    public boolean visitTerminal(
            final TerminalNode node, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return terminalFilter.visitTerminal(node, delegationCtx);
    }

    @Override
    public int getRequiredType() {
        return CountlangLexer.ID;
    }

    @Override
    public void setText(final String text) {
        statement.setSymbol(text);
    }

    @Override
    public void addExpression(final ExpressionNode expression) {
        statement.setSampledDistribution(expression);
    }
}
