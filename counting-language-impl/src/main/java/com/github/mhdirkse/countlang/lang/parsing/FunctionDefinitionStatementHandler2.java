package com.github.mhdirkse.countlang.lang.parsing;

import java.util.List;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.Statement;
import com.github.mhdirkse.countlang.execution.ProgramRuntimeException;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class FunctionDefinitionStatementHandler2 extends AbstractStatementGroupHandler2
implements StatementSource, TerminalStrategyCallback2 {
    private final TerminalStrategy2 terminalStrategy;
    private FunctionDefinitionStatement statement;

    @Override
    public Statement getStatement() {
        return statement;
    }

    FunctionDefinitionStatementHandler2(final int line, final int column) {
        statement = new FunctionDefinitionStatement(line, column);
        terminalStrategy = new TerminalStrategy2(this);
    }

    @Override
    void addStatement(final Statement childStatement) {
        statement.addStatement(childStatement);
    }

    @Override
    public boolean enterFunctionDefinitionStatement(
            CountlangParser.FunctionDefinitionStatementContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        throw new ProgramRuntimeException(statement.getLine(), statement.getColumn(), "Nested function definitions not allowed");
    }

    @Override
    public boolean visitTerminal(
            @NotNull TerminalNode node,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return terminalStrategy.visitTerminal(node, delegationCtx);
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
        delegationCtx.addFirst(new VarDeclsHandler2());
        return true;
    }

    @Override
    public boolean exitVarDecls(
            @NotNull CountlangParser.VarDeclsContext antlrCtx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        if (delegationCtx.isFirst()) {
            return false;
        } else {
            List<String> formalParameters = ((VarDeclsHandler2) delegationCtx.getPreviousHandler()).getFormalParameters();
            for (String formalParameter : formalParameters) {
                statement.addFormalParameter(formalParameter);
            }
            delegationCtx.removeAllPreceeding();
            return true;
        }
    }
}
