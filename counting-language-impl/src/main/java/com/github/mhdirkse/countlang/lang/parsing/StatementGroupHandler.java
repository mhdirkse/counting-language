package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.lang.CountlangParser;

import org.antlr.v4.runtime.misc.NotNull;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.Statement;

class StatementGroupHandler extends AbstractCountlangListenerHandler {
    private StatementGroup statementGroup;

    StatementGroup getStatementGroup() {
        return statementGroup;
    }

    StatementGroupHandler(final int line, final int column) {
        super(false);
        statementGroup = new StatementGroup(line, column);
    }

    @Override
    public boolean enterPrintStatement(
            @NotNull CountlangParser.PrintStatementContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new PrintStatementHandler(line, column));
        return true;
    }

    @Override
    public boolean enterAssignmentStatement(
            @NotNull CountlangParser.AssignmentStatementContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new AssignmentStatementHandler(line, column));
        return true;
    }

    @Override
    public boolean enterReturnStatement(
            @NotNull CountlangParser.ReturnStatementContext ctx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegationCtx.addFirst(new ReturnStatementHandler(line, column));
        return true;
    }

    @Override
    public boolean enterFunctionDefinitionStatement(
            CountlangParser.FunctionDefinitionStatementContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new FunctionDefinitionStatementHandler(line, column));
        return true;
    }

    @Override
    public boolean exitPrintStatement(
            @NotNull CountlangParser.PrintStatementContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleStatementExit(delegationCtx);
    }

    private boolean handleStatementExit(HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        if (delegationCtx.isFirst()) {
            return false;
        } else {
            Statement statement = ((StatementSource) delegationCtx.getPreviousHandler()).getStatement();
            statementGroup.addStatement(statement);
            delegationCtx.removeAllPreceeding();
            return true;
        }
    }

    @Override
    public boolean exitAssignmentStatement(
            @NotNull CountlangParser.AssignmentStatementContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleStatementExit(delegationCtx);
    }

    @Override
    public boolean exitReturnStatement(
            @NotNull CountlangParser.ReturnStatementContext ctx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleStatementExit(delegationCtx);
    }

    @Override
    public boolean exitFunctionDefinitionStatement(
            @NotNull CountlangParser.FunctionDefinitionStatementContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleStatementExit(delegationCtx);
    }
}
