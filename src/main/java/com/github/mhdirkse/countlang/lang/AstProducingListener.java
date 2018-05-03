package com.github.mhdirkse.countlang.lang;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.countlang.ast.Program;

final class AstProducingListener extends CountlangBaseListener {
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
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        program = new Program(line, column);
    }

    @Override
    public void exitProg(final CountlangParser.ProgContext ctx) {
        isFinished = true;
    }

    @Override
    public void enterAssignmentStatement(final CountlangParser.AssignmentStatementContext ctx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        inStatement = new InAssignmentStatement(line, column);
    }

    @Override
    public void exitAssignmentStatement(final CountlangParser.AssignmentStatementContext ctx) {
        program.addStatement(inStatement.getStatement());
        inStatement = null;
    }

    @Override
    public void enterPrintStatement(final CountlangParser.PrintStatementContext ctx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        inStatement = new InPrintStatement(line, column);
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
        if (inStatement != null) {
            inStatement.enterMultDifExpression(ctx);
        } else {
        	throw new IllegalArgumentException("Unexpected entry of multiply or divide expression");
        }
    }

    @Override
    public void exitMultDifExpression(final CountlangParser.MultDifExpressionContext ctx) {
        if (inStatement != null) {
            inStatement.exitMultDifExpression(ctx);
        } else {
        	throw new IllegalArgumentException("Unexpected leave of multiply or divide expression");
        }
    }

    @Override
    public void enterPlusMinusExpression(final CountlangParser.PlusMinusExpressionContext ctx) {
        if (inStatement != null) {
            inStatement.enterPlusMinusExpression(ctx);
        } else {
        	throw new IllegalArgumentException("Unexpected entry of plus or minus expression");
        }
    }

    @Override
    public void exitPlusMinusExpression(final CountlangParser.PlusMinusExpressionContext ctx) {
        if (inStatement != null) {
            inStatement.exitPlusMinusExpression(ctx);
        } else {
        	throw new IllegalArgumentException("Unexpected leave of plus or minus expression");
        }
    }

    @Override
    public void enterSymbolReferenceExpression(final CountlangParser.SymbolReferenceExpressionContext ctx) {
        if (inStatement != null) {
            inStatement.enterSymbolReferenceExpression(ctx);
        }
    }

    @Override
    public void exitSymbolReferenceExpression(final CountlangParser.SymbolReferenceExpressionContext ctx) {
        if (inStatement != null) {
             inStatement.exitSymbolReferenceExpression(ctx);
        }
    }

    @Override
    public void enterValueExpression(final CountlangParser.ValueExpressionContext ctx) {
        if (inStatement != null) {
            inStatement.enterValueExpression(ctx);
        }
    }

    @Override
    public void exitValueExpression(final CountlangParser.ValueExpressionContext ctx) {
        if (inStatement != null) {
            inStatement.exitValueExpression(ctx);
        }
    }

    @Override
    public void visitTerminal(final TerminalNode terminalNode) {
        if (inStatement != null) {
            inStatement.visitTerminal(terminalNode);
        }
    }
}
