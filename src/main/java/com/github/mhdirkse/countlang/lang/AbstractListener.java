package com.github.mhdirkse.countlang.lang;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.ast.Program;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.ValueExpression;

public class AbstractListener extends CountlangBaseListener implements AstNode.Visitor {
    CountlangBaseListener delegate;

    AbstractListener() {
    }

    @Override
    public void enterPrintStatement(@NotNull CountlangParser.PrintStatementContext ctx) {
        if (delegate != null) {
            delegate.enterPrintStatement(ctx);
        }
    }

    @Override
    public void exitPrintStatement(@NotNull CountlangParser.PrintStatementContext ctx) {
        if (delegate != null) {
            delegate.exitPrintStatement(ctx);
        }
    }

    @Override
    public void enterValueExpression(@NotNull CountlangParser.ValueExpressionContext ctx) {
        if (delegate != null) {
            delegate.enterValueExpression(ctx);
        }
    }

    @Override
    public void exitValueExpression(@NotNull CountlangParser.ValueExpressionContext ctx) {
        if (delegate != null) {
            delegate.exitValueExpression(ctx);
        }
    }

    @Override
    public void enterPlusMinusExpression(@NotNull CountlangParser.PlusMinusExpressionContext ctx) {
        if (delegate != null) {
            delegate.enterPlusMinusExpression(ctx);
        }
    }

    @Override
    public void exitPlusMinusExpression(@NotNull CountlangParser.PlusMinusExpressionContext ctx) {
        if (delegate != null) {
            delegate.exitPlusMinusExpression(ctx);
        }
    }

    @Override
    public void enterAssignmentStatement(@NotNull CountlangParser.AssignmentStatementContext ctx) {
        if (delegate != null) {
            delegate.enterAssignmentStatement(ctx);
        }
    }

    @Override
    public void exitAssignmentStatement(@NotNull CountlangParser.AssignmentStatementContext ctx) {
        if (delegate != null) {
            delegate.exitAssignmentStatement(ctx);
        }
    }

    @Override
    public void enterSymbolReferenceExpression(@NotNull CountlangParser.SymbolReferenceExpressionContext ctx) {
        if (delegate != null) {
            delegate.enterSymbolReferenceExpression(ctx);
        }
    }

    @Override
    public void exitSymbolReferenceExpression(@NotNull CountlangParser.SymbolReferenceExpressionContext ctx) {
        if (delegate != null) {
            delegate.exitSymbolReferenceExpression(ctx);
        }
    }

    @Override
    public void enterBracketExpression(@NotNull CountlangParser.BracketExpressionContext ctx) {
        if (delegate != null) {
            delegate.enterBracketExpression(ctx);
        }
    }

    @Override
    public void exitBracketExpression(@NotNull CountlangParser.BracketExpressionContext ctx) {
        if (delegate != null) {
            delegate.exitBracketExpression(ctx);
        }
    }

    @Override
    public void enterMultDifExpression(@NotNull CountlangParser.MultDifExpressionContext ctx) {
        if (delegate != null) {
            delegate.enterMultDifExpression(ctx);
        }
    }

    @Override
    public void exitMultDifExpression(@NotNull CountlangParser.MultDifExpressionContext ctx) {
        if (delegate != null) {
            delegate.exitMultDifExpression(ctx);
        }
    }

    @Override
    public void enterProg(@NotNull CountlangParser.ProgContext ctx) {
        if (delegate != null) {
            delegate.enterProg(ctx);
        }
    }

    @Override
    public void exitProg(@NotNull CountlangParser.ProgContext ctx) {
        if (delegate != null) {
            delegate.exitProg(ctx);
        }
    }

    @Override
    public void visitTerminal(@NotNull TerminalNode node) {
        if (delegate != null) {
            delegate.visitTerminal(node);
        }
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
