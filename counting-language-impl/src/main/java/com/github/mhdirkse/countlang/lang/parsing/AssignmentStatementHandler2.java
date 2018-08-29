package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.Statement;
import com.github.mhdirkse.countlang.execution.Symbol;
import com.github.mhdirkse.countlang.lang.CountlangLexer;

class AssignmentStatementHandler2 extends AbstractExpressionHandler2
implements StatementSource, TerminalStrategyCallback2 {
    private final TerminalStrategy2 terminalStrategy;
    private AssignmentStatement statement;

    @Override
    public Statement getStatement() {
        return statement;
    }

    AssignmentStatementHandler2(final int line, final int column) {
        statement = new AssignmentStatement(line, column);
        terminalStrategy = new TerminalStrategy2(this);
    }

    @Override
    public boolean visitTerminal(
            final TerminalNode node, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return terminalStrategy.visitTerminal(node, delegationCtx);
    }

    @Override
    public int getRequiredType() {
        return CountlangLexer.ID;
    }

    @Override
    public void setText(final String text) {
        statement.setLhs(new Symbol(text));
    }

    @Override
    public void addExpression(final ExpressionNode expression) {
        statement.setRhs(expression);
    }
}
