package com.github.mhdirkse.countlang.lang;

import org.antlr.v4.runtime.misc.NotNull;

import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.ast.Program;

class ProgListener extends AbstractListener {
    private final Program program;
    private final AstNode.Visitor parent;

    ProgListener(final int line, final int column, final AstNode.Visitor parent) {
        program = new Program(line, column);
        this.parent = parent;
    }

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
    public void visitPrintStatement(final PrintStatement statement) {
        program.addStatement(statement);
        delegate = null;
    }

    @Override
    public void visitAssignmentStatement(final AssignmentStatement statement) {
        program.addStatement(statement);
        delegate = null;
    }

    @Override
    public void exitProgImpl(@NotNull CountlangParser.ProgContext ctx) {
        parent.visitProgram(program);
    }
}
