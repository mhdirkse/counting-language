package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.misc.NotNull;

import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.ast.ReturnStatement;
import com.github.mhdirkse.countlang.ast.Statement;
import com.github.mhdirkse.countlang.lang.CountlangParser;
import com.github.mhdirkse.countlang.lang.CountlangParser.FunctionDefinitionStatementContext;

abstract class AbstractStatementListener extends AbstractListener {
    @Override
    void enterPrintStatementImpl(@NotNull CountlangParser.PrintStatementContext ctx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegate = new PrintStatementListener(line, column, this);
    }

    @Override
    void enterAssignmentStatementImpl(@NotNull CountlangParser.AssignmentStatementContext ctx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegate = new AssignmentStatementListener(line, column, this);
    }

    @Override
    void enterReturnStatementImpl(@NotNull CountlangParser.ReturnStatementContext ctx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegate = new ReturnStatementListener(line, column, this);
    }

    @Override
    public void enterFunctionDefinitionStatement(FunctionDefinitionStatementContext ctx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegate = new FunctionDefinitionStatementListener(line, column, this);
    }

    @Override
    public void visitPrintStatement(final PrintStatement statement) {
        handleStatement(statement);
        delegate = null;
    }

    @Override
    public void visitAssignmentStatement(final AssignmentStatement statement) {
        handleStatement(statement);
        delegate = null;
    }

    @Override
    public void visitReturnStatement(ReturnStatement statement) {
        handleStatement(statement);
        delegate = null;
    }

    @Override
    public void visitFunctionDefinitionStatement(FunctionDefinitionStatement statement) {
        handleStatement(statement);
        delegate = null;
    }

    abstract void handleStatement(final Statement statement);
}
