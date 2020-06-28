package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.misc.NotNull;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.StackStrategy;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class RootHandler extends AbstractCountlangListenerHandler {
    private StatementGroup statementGroup = null;

    RootHandler() {
        super(false);
    }

    StatementGroup getProgram() {
        return statementGroup;
    }

    @Override
    public boolean enterProg(
            @NotNull CountlangParser.ProgContext antlrCtx, final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return true;
    }

    @Override
    public boolean exitProg(
            @NotNull CountlangParser.ProgContext antlrCtx, final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return true;
    }

    @Override
    public boolean enterStatements(
            @NotNull CountlangParser.StatementsContext ctx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegationCtx.addFirst(new StatementGroupHandlerNoCompound(
                StackStrategy.NO_NEW_FRAME, line, column));
        return true;
    }

    @Override
    public boolean exitStatements(
            @NotNull CountlangParser.StatementsContext ctx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        statementGroup = ((StatementGroupHandlerNoCompound) delegationCtx.getPreviousHandler()).getStatementGroup();
        delegationCtx.removeAllPreceeding();
        return true;        
    }
}
