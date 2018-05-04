package com.github.mhdirkse.countlang.lang;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.ast.Program;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.ValueExpression;

class AbstractListener extends CountlangBaseListener implements AstNode.Visitor {
    CountlangBaseListener delegate;

    @Override
    public final void enterPrintStatement(@NotNull CountlangParser.PrintStatementContext ctx) {
        if (delegate != null) {
            delegate.enterPrintStatement(ctx);
        } else {
            enterPrintStatementImpl(ctx);
        }
    }

    void enterPrintStatementImpl(@NotNull CountlangParser.PrintStatementContext ctx) {
    }

    @Override
    public final void exitPrintStatement(@NotNull CountlangParser.PrintStatementContext ctx) {
        if (delegate != null) {
            delegate.exitPrintStatement(ctx);
        } else {
            exitPrintStatementImpl(ctx);
        }
    }

    void exitPrintStatementImpl(@NotNull CountlangParser.PrintStatementContext ctx) {
    }

    @Override
    public final void enterValueExpression(@NotNull CountlangParser.ValueExpressionContext ctx) {
        if (delegate != null) {
            delegate.enterValueExpression(ctx);
        } else {
            enterValueExpressionImpl(ctx);
        }
    }

    void enterValueExpressionImpl(@NotNull CountlangParser.ValueExpressionContext ctx) {
    }

    @Override
    public final void exitValueExpression(@NotNull CountlangParser.ValueExpressionContext ctx) {
        if (delegate != null) {
            delegate.exitValueExpression(ctx);
        } else {
            exitValueExpressionImpl(ctx);
        }
    }

    void exitValueExpressionImpl(@NotNull CountlangParser.ValueExpressionContext ctx) {
    }

    @Override
    public final void enterPlusMinusExpression(@NotNull CountlangParser.PlusMinusExpressionContext ctx) {
        if (delegate != null) {
            delegate.enterPlusMinusExpression(ctx);
        } else {
            enterPlusMinusExpressionImpl(ctx);
        }
    }

    void enterPlusMinusExpressionImpl(@NotNull CountlangParser.PlusMinusExpressionContext ctx) {
    }

    @Override
    public final void exitPlusMinusExpression(@NotNull CountlangParser.PlusMinusExpressionContext ctx) {
        if (delegate != null) {
            delegate.exitPlusMinusExpression(ctx);
        } else {
            exitPlusMinusExpressionImpl(ctx);
        }
    }

    void exitPlusMinusExpressionImpl(@NotNull CountlangParser.PlusMinusExpressionContext ctx) {
    }

    @Override
    public final void enterAssignmentStatement(@NotNull CountlangParser.AssignmentStatementContext ctx) {
        if (delegate != null) {
            delegate.enterAssignmentStatement(ctx);
        } else {
            enterAssignmentStatementImpl(ctx);
        }
    }

    void enterAssignmentStatementImpl(@NotNull CountlangParser.AssignmentStatementContext ctx) {
    }

    @Override
    public final void exitAssignmentStatement(@NotNull CountlangParser.AssignmentStatementContext ctx) {
        if (delegate != null) {
            delegate.exitAssignmentStatement(ctx);
        } else {
            exitAssignmentStatementImpl(ctx);
        }
    }

    void exitAssignmentStatementImpl(@NotNull CountlangParser.AssignmentStatementContext ctx) {
    }

    @Override
    public final void enterSymbolReferenceExpression(@NotNull CountlangParser.SymbolReferenceExpressionContext ctx) {
        if (delegate != null) {
            delegate.enterSymbolReferenceExpression(ctx);
        } else {
            enterSymbolReferenceExpressionImpl(ctx);
        }
    }

    void enterSymbolReferenceExpressionImpl(@NotNull CountlangParser.SymbolReferenceExpressionContext ctx) {
    }

    @Override
    public final void exitSymbolReferenceExpression(@NotNull CountlangParser.SymbolReferenceExpressionContext ctx) {
        if (delegate != null) {
            delegate.exitSymbolReferenceExpression(ctx);
        } else {
            exitSymbolReferenceExpressionImpl(ctx);
        }
    }

    void exitSymbolReferenceExpressionImpl(@NotNull CountlangParser.SymbolReferenceExpressionContext ctx) {
    }

    @Override
    public final void enterBracketExpression(@NotNull CountlangParser.BracketExpressionContext ctx) {
        if (delegate != null) {
            delegate.enterBracketExpression(ctx);
        } else {
            enterBracketExpressionImpl(ctx);
        }
    }

    void enterBracketExpressionImpl(@NotNull CountlangParser.BracketExpressionContext ctx) {
    }

    @Override
    public final void exitBracketExpression(@NotNull CountlangParser.BracketExpressionContext ctx) {
        if (delegate != null) {
            delegate.exitBracketExpression(ctx);
        } else {
            exitBracketExpressionImpl(ctx);
        }
    }

    void exitBracketExpressionImpl(@NotNull CountlangParser.BracketExpressionContext ctx) {
    }

    @Override
    public final void enterMultDifExpression(@NotNull CountlangParser.MultDifExpressionContext ctx) {
        if (delegate != null) {
            delegate.enterMultDifExpression(ctx);
        } else {
            enterMultDifExpressionImpl(ctx);
        }
    }

    void enterMultDifExpressionImpl(@NotNull CountlangParser.MultDifExpressionContext ctx) {
    }

    @Override
    public final void exitMultDifExpression(@NotNull CountlangParser.MultDifExpressionContext ctx) {
        if (delegate != null) {
            delegate.exitMultDifExpression(ctx);
        } else {
            exitMultDifExpression(ctx);
        }
    }

    void exitMultDifExpressionImpl(@NotNull CountlangParser.MultDifExpressionContext ctx) {
    }

    @Override
    public final void enterProg(@NotNull CountlangParser.ProgContext ctx) {
        if (delegate != null) {
            delegate.enterProg(ctx);
        } else {
            enterProgImpl(ctx);
        }
    }

    void enterProgImpl(@NotNull CountlangParser.ProgContext ctx) {
    }

    @Override
    public final void exitProg(@NotNull CountlangParser.ProgContext ctx) {
        if (delegate != null) {
            delegate.exitProg(ctx);
        } else {
            exitProgImpl(ctx);
        }
    }

    void exitProgImpl(@NotNull CountlangParser.ProgContext ctx) {
    }

    @Override
    public final void visitTerminal(@NotNull TerminalNode node) {
        if (delegate != null) {
            delegate.visitTerminal(node);
        } else {
            visitTerminalImpl(node);
        }
    }

    void visitTerminalImpl(@NotNull TerminalNode node) {
    }

    @Override public void visitErrorNode(@NotNull ErrorNode node) {
        throw new IllegalArgumentException("Parse error encountered");
    }

    @Override
    public void visitProgram(final Program program) {
    }

    @Override
    public void visitAssignmentStatement(final AssignmentStatement statement) {
    }

    @Override
    public void visitPrintStatement(final PrintStatement statement) {
    }

    @Override
    public void visitCompositeExpression(final CompositeExpression expression) {
    }

    @Override
    public void visitSymbolExpression(final SymbolExpression expression) {
    }

    @Override
    public void visitValueExpression(final ValueExpression expression) {
    }
}
