package com.github.mhdirkse.countlang.lang.parsing;

import java.util.List;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.Statement;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class FunctionDefinitionStatementHandler extends AbstractCountlangListenerHandler
implements StatementSource, TerminalFilterCallback {
    private final TerminalFilter terminalFilter;
    private FunctionDefinitionStatement statement;

    @Override
    public Statement getStatement() {
        return statement;
    }

    FunctionDefinitionStatementHandler(final int line, final int column) {
        super(false);
        statement = new FunctionDefinitionStatement(line, column);
        terminalFilter = new TerminalFilter(this);
    }

    @Override
    public boolean enterStatements(
            @NotNull CountlangParser.StatementsContext ctx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegationCtx.addFirst(new StatementGroupHandlerNoCompound(
                StatementGroup.StackStrategy.NO_NEW_FRAME, line, column));
        return true;   
    }

    @Override
    public boolean exitStatements(
            @NotNull CountlangParser.StatementsContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        if(delegationCtx.isFirst()) {
            return false;
        } else {
            StatementGroup statements = ((StatementGroupHandlerNoCompound) delegationCtx.getPreviousHandler()).getStatementGroup();
            statement.setStatements(statements);
            delegationCtx.removeAllPreceeding();
            return true;
        }
    }

    @Override
    public boolean visitTerminal(
            @NotNull TerminalNode node,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return terminalFilter.visitTerminal(node, delegationCtx);
    }

    @Override
    public int getRequiredType() {
        return CountlangParser.ID;
    }

    @Override
    public void setText(final String text) {
        statement.setName(text);
    }

    @Override
    public boolean enterVarDecls(
            @NotNull CountlangParser.VarDeclsContext antlrCtx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        delegationCtx.addFirst(new VarDeclsHandler());
        return true;
    }

    @Override
    public boolean exitVarDecls(
            @NotNull CountlangParser.VarDeclsContext antlrCtx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        if (delegationCtx.isFirst()) {
            return false;
        } else {
            List<FormalParameter> formalParameters = ((VarDeclsHandler) delegationCtx.getPreviousHandler()).getFormalParameters();
            for (FormalParameter formalParameter : formalParameters) {
                statement.addFormalParameter(formalParameter.getName(), formalParameter.getCountlangType());
            }
            delegationCtx.removeAllPreceeding();
            return true;
        }
    }
}
