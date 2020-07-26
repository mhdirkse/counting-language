package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.misc.NotNull;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.Statement;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class CompoundStatementHandler extends AbstractCountlangListenerHandler implements StatementSource {
    private StatementGroup statementGroup;

    @Override
    public Statement getStatement() {
        return statementGroup;
    }

    CompoundStatementHandler(final int line, final int column) {
        super(false);
    }

    @Override
    public boolean enterStatements(
            @NotNull CountlangParser.StatementsContext ctx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegationCtx.addFirst(new StatementGroupHandler(line, column));
        return true;
    }

    @Override
    public boolean exitStatements(
            @NotNull CountlangParser.StatementsContext ctx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        statementGroup = ((StatementGroupHandler) delegationCtx.getPreviousHandler()).getStatementGroup();
        delegationCtx.removeAllPreceeding();
        return true;        
    }
}