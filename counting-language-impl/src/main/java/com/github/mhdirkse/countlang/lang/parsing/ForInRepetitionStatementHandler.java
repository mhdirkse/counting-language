package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.SimpleLhs;
import com.github.mhdirkse.countlang.lang.CountlangLexer;

class ForInRepetitionStatementHandler extends AbstractForInRepetitionStatementHandler implements TerminalFilterCallback {
	private TerminalFilter terminalFilter;

	ForInRepetitionStatementHandler(int line, int column) {
		super(line, column);
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
        SimpleLhs simpleLhs = new SimpleLhs(statement.getLine(), statement.getColumn());
        simpleLhs.setSymbol(text);
        statement.setLhs(simpleLhs);
    }
}
