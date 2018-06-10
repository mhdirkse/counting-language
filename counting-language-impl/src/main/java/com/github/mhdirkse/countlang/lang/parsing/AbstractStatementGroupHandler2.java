package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.misc.NotNull;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.Statement;
import com.github.mhdirkse.countlang.lang.CountlangParser;

abstract class AbstractStatementGroupHandler2 extends AbstractCountlangListenerHandler {
    AbstractStatementGroupHandler2() {
        super(false);
    }

    @Override
    public boolean enterPrintStatement(
            @NotNull CountlangParser.PrintStatementContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new PrintStatementHandler2(line, column));
        return true;
    }

    @Override
    public boolean enterAssignmentStatement(
            @NotNull CountlangParser.AssignmentStatementContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new AssignmentStatementHandler2(line, column));
        return true;
    }

    @Override
    public boolean enterReturnStatement(
            @NotNull CountlangParser.ReturnStatementContext ctx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegationCtx.addFirst(new ReturnStatementHandler2(line, column));
        return true;
    }

    @Override
    public boolean enterFunctionDefinitionStatement(
            CountlangParser.FunctionDefinitionStatementContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new FunctionDefinitionStatementHandler2(line, column));
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
            addStatement(statement);
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

    abstract void addStatement(final Statement statement);
}
