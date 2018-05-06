package com.github.mhdirkse.countlang.lang;

import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.Expression;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.lang.CountlangParser.FunctionCallExpressionContext;

abstract class AbstractChildExpressionListener extends AbstractListener {
    @Override
    void enterBracketExpressionImpl(CountlangParser.BracketExpressionContext ctx) {
        // Nothing to do.
    }

    @Override
    void enterFunctionCallExpressionImpl(FunctionCallExpressionContext ctx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegate = new FunctionCallExpressionListener(line, column, this);
    }

    @Override
    void enterMultDifExpressionImpl(CountlangParser.MultDifExpressionContext ctx) {
        setOperatorExpressionListener(ctx);
    }

    private void setOperatorExpressionListener(final CountlangParser.ExprContext ctx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegate = new OperatorExpressionListener(line, column, this);        
    }

    @Override
    void enterPlusMinusExpressionImpl(final CountlangParser.PlusMinusExpressionContext ctx) {
        setOperatorExpressionListener(ctx);
    }

    @Override
    void enterSymbolReferenceExpressionImpl(final CountlangParser.SymbolReferenceExpressionContext ctx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegate = new SymbolReferenceExpressionListener(line, column, this);
    }

    @Override
    void enterValueExpressionImpl(final CountlangParser.ValueExpressionContext ctx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegate = new ValueExpressionListener(line, column, this);
    }

    @Override
    public void visitFunctionCallExpression(final FunctionCallExpression expression) {
        handleChildExpression(expression);
        delegate = null;
    }

    @Override
    public void visitCompositeExpression(CompositeExpression expression) {
        handleChildExpression(expression);
        delegate = null;
    }

    @Override
    public void visitSymbolExpression(SymbolExpression expression) {
        handleChildExpression(expression);
        delegate = null;
    }

    @Override
    public void visitValueExpression(ValueExpression expression) {
        handleChildExpression(expression);
        delegate = null;
    }

    abstract void handleChildExpression(final Expression expression);
}
