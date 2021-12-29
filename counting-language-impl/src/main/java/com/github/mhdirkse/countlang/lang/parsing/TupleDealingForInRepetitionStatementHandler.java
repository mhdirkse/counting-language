package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.TupleDealingLhs;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class TupleDealingForInRepetitionStatementHandler extends AbstractForInRepetitionStatementHandler {
    private LeftHandSideStrategy lhsStrategy = new LeftHandSideStrategy();

	TupleDealingForInRepetitionStatementHandler(int line, int column) {
		super(line, column);
	}

    @Override
    public boolean enterLhsItem(CountlangParser.LhsItemContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return lhsStrategy.enterLhsItem(antlrCtx, delegationCtx);
    }

    @Override
    public boolean exitLhsItem(CountlangParser.LhsItemContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return lhsStrategy.exitLhsItem(antlrCtx, delegationCtx);
    }

    @Override
    public boolean visitTerminal(
            final TerminalNode node, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        if(lhsStrategy.isInLhsItem()) {
            return lhsStrategy.visitTerminal(node, delegationCtx);
        } else if(node.getText().equals("in")) {
            TupleDealingLhs lhs = new TupleDealingLhs(statement.getLine(), statement.getColumn());
            lhs.setChildren(lhsStrategy.getLhsItems());
            statement.setLhs(lhs);
            return true;
        } else {
            return false;
        }
    }
}
