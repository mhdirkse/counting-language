package com.github.mhdirkse.countlang.lang;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.countlang.ast.Program;

public final class AstProducingListener extends CountlangBaseListener {
    private Program program = null;
    private boolean isFinished = false;
    private InStatement inStatement = null;

    public boolean isFinished() {
        return isFinished;
    }

    public Program getProgram() {
        return program;
    }

    @Override
    public void enterProg(final CountlangParser.ProgContext ctx) {
        program = new Program();
    }

    @Override
    public void exitProg(final CountlangParser.ProgContext ctx) {
        isFinished = true;
    }

    @Override
    public void enterAssignmentStatement(final CountlangParser.AssignmentStatementContext ctx) {
        inStatement = new InAssignmentStatement();
    }

    @Override
    public void exitAssignmentStatement(final CountlangParser.AssignmentStatementContext ctx) {
        program.addStatement(inStatement.getStatement());
        inStatement = null;
    }

    @Override
    public void enterPrintStatement(final CountlangParser.PrintStatementContext ctx) {
        inStatement = new InPrintStatement();
    }

    @Override
    public void exitPrintStatement(final CountlangParser.PrintStatementContext ctx) {
        program.addStatement(inStatement.getStatement());
        inStatement = null;
    }

    @Override
    public void visitErrorNode(final ErrorNode errorNode) {
        // TODO: Improve error handling.
        throw new IllegalArgumentException("Parse error encountered");
    }

    // TODO: Correct spelling in the grammar.
    @Override
    public void enterMultDifExpression(final CountlangParser.MultDifExpressionContext ctx) {
        inStatement.enterMultDifExpression(ctx);
    }

    @Override
    public void exitMultDifExpression(final CountlangParser.MultDifExpressionContext ctx) {
        inStatement.exitMultDifExpression(ctx);
    }

    @Override
    public void enterPlusMinusExpression(final CountlangParser.PlusMinusExpressionContext ctx) {
        inStatement.enterPlusMinusExpression(ctx);
    }

    @Override
    public void exitPlusMinusExpression(final CountlangParser.PlusMinusExpressionContext ctx) {
        inStatement.exitPlusMinusExpression(ctx);
    }

    @Override
    public void enterSymbolReferenceExpression(final CountlangParser.SymbolReferenceExpressionContext ctx) {
        inStatement.enterSymbolReferenceExpression(ctx);
    }

    @Override
    public void exitSymbolReferenceExpression(final CountlangParser.SymbolReferenceExpressionContext ctx) {
        inStatement.exitSymbolReferenceExpression(ctx);
    }

    @Override
    public void enterValueExpression(final CountlangParser.ValueExpressionContext ctx) {
        inStatement.enterValueExpression(ctx);
    }

    @Override
    public void exitValueExpression(final CountlangParser.ValueExpressionContext ctx) {
        inStatement.exitValueExpression(ctx);
    }

    @Override
    public void visitTerminal(final TerminalNode terminalNode) {
        inStatement.visitTerminal(terminalNode);
    }
}
