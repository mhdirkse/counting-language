package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.misc.NotNull;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.lang.CountlangParser;
import com.github.mhdirkse.countlang.lang.CountlangParser.UnaryMinusExpressionContext;

abstract class AbstractExpressionHandler2 extends AbstractCountlangListenerHandler {
    AbstractExpressionHandler2() {
        super(false);
    }

    @Override
    public boolean enterFunctionCallExpression(
            CountlangParser.FunctionCallExpressionContext antlrCtx, final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new FunctionCallExpressionHandler2(line, column));
        return true;
    }

    @Override
    public boolean enterMultDifExpression(
            @NotNull CountlangParser.MultDifExpressionContext antlrCtx, final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return setOperatorExpressionListener(antlrCtx, delegationCtx);
    }

    private boolean setOperatorExpressionListener(
            @NotNull final CountlangParser.ExprContext ctx, final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegationCtx.addFirst(new OperatorExpressionHandler2(line, column));
        return true;   
    }

    @Override
    public boolean enterPlusMinusExpression(
            @NotNull final CountlangParser.PlusMinusExpressionContext antlrCtx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return setOperatorExpressionListener(antlrCtx, delegationCtx);
    }

    @Override
    public boolean enterUnaryMinusExpression(
    		UnaryMinusExpressionContext antlrCtx,
    		HandlerStackContext<CountlangListenerHandler> delegationCtx) {
    	int line = antlrCtx.start.getLine();
    	int column = antlrCtx.start.getCharPositionInLine();
    	delegationCtx.addFirst(new UnaryMinusExpressionHandler(line, column));
    	return true;
    }

    @Override
    public boolean enterSymbolReferenceExpression(
            @NotNull final CountlangParser.SymbolReferenceExpressionContext ctx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegationCtx.addFirst(new SymbolReferenceExpressionHandler2(line, column));
        return true;
    }

    @Override
    public boolean enterValueExpression(
            final CountlangParser.ValueExpressionContext ctx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegationCtx.addFirst(new ValueExpressionHandler2(line, column));
        return true;
    }

    @Override
    public boolean exitFunctionCallExpression(
            CountlangParser.FunctionCallExpressionContext antlrCtx, final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleExpressionExit(delegationCtx);
    }

    private boolean handleExpressionExit(final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        if(delegationCtx.isFirst()) {
            return false;
        } else {
            ExpressionNode expression = ((ExpressionSource) delegationCtx.getPreviousHandler()).getExpression();
            addExpression(expression);
            delegationCtx.removeAllPreceeding();
            return true;
        }
    }

    @Override
    public boolean exitMultDifExpression(
            @NotNull CountlangParser.MultDifExpressionContext antlrCtx, final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleExpressionExit(delegationCtx);
    }

    @Override
    public boolean exitPlusMinusExpression(
            @NotNull final CountlangParser.PlusMinusExpressionContext antlrCtx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleExpressionExit(delegationCtx);
    }

    @Override
    public boolean exitUnaryMinusExpression(
    		@NotNull UnaryMinusExpressionContext antlrCtx,
    		HandlerStackContext<CountlangListenerHandler> delegationCtx) {
    	return handleExpressionExit(delegationCtx);
    }

    @Override
    public boolean exitSymbolReferenceExpression(
            @NotNull final CountlangParser.SymbolReferenceExpressionContext ctx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleExpressionExit(delegationCtx);
    }

    @Override
    public boolean exitValueExpression(
            final CountlangParser.ValueExpressionContext ctx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleExpressionExit(delegationCtx);
    }

    abstract void addExpression(final ExpressionNode expression);
}
