/*
 * Copyright Martijn Dirkse 2020
 *
 * This file is part of counting-language.
 *
 * counting-language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.misc.NotNull;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.lang.CountlangParser;
import com.github.mhdirkse.countlang.lang.CountlangParser.UnaryMinusExpressionContext;

abstract class AbstractExpressionHandler extends AbstractCountlangListenerHandler {
    AbstractExpressionHandler() {
        super(false);
    }

    @Override
    public boolean enterFunctionCallExpression(
            CountlangParser.FunctionCallExpressionContext antlrCtx, final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new FunctionCallExpressionHandler(line, column));
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
        delegationCtx.addFirst(new GeneralOperatorExpressionHandler(line, column));
        return true;   
    }

    @Override
    public boolean enterPlusMinusExpression(
            @NotNull final CountlangParser.PlusMinusExpressionContext antlrCtx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return setOperatorExpressionListener(antlrCtx, delegationCtx);
    }

    @Override
    public boolean enterOrExpression(
            @NotNull final CountlangParser.OrExpressionContext antlrCtx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return setOperatorExpressionListener(antlrCtx, delegationCtx);
    }

    @Override
    public boolean enterAndExpression(
            @NotNull final CountlangParser.AndExpressionContext antlrCtx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return setOperatorExpressionListener(antlrCtx, delegationCtx);
    }

    @Override
    public boolean enterNotExpression(
            @NotNull final CountlangParser.NotExpressionContext antlrCtx,
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
    public boolean enterCompExpression(
            @NotNull final CountlangParser.CompExpressionContext antlrCtx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return setOperatorExpressionListener(antlrCtx, delegationCtx);
    }

    @Override
    public boolean enterSymbolReferenceExpression(
            @NotNull final CountlangParser.SymbolReferenceExpressionContext ctx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegationCtx.addFirst(new SymbolReferenceExpressionHandler(line, column));
        return true;
    }

    @Override
    public boolean enterValueExpression(
            final CountlangParser.ValueExpressionContext ctx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegationCtx.addFirst(new ValueExpressionHandler(line, column));
        return true;
    }

    @Override
    public boolean enterDistributionExpression(
            final CountlangParser.DistributionExpressionContext ctx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegationCtx.addFirst(new DistributionExpressionHandler(line, column));
        return true;
    }

    @Override
    public boolean enterDistributionKnownExpression (
            final CountlangParser.DistributionKnownExpressionContext ctx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegationCtx.addFirst(new DistributionKnownExpressionHandler(line, column));
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
    public boolean exitAndExpression(
            @NotNull final CountlangParser.AndExpressionContext antlrCtx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleExpressionExit(delegationCtx);
    }

    @Override
    public boolean exitOrExpression(
            @NotNull final CountlangParser.OrExpressionContext antlrCtx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleExpressionExit(delegationCtx);
    }

    @Override
    public boolean exitNotExpression(
            @NotNull final CountlangParser.NotExpressionContext antlrCtx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleExpressionExit(delegationCtx);
    }

    @Override
    public boolean exitCompExpression(
            @NotNull final CountlangParser.CompExpressionContext antlrCtx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
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

    @Override
    public boolean exitDistributionExpression(
            final CountlangParser.DistributionExpressionContext ctx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleExpressionExit(delegationCtx);
    }

    @Override
    public boolean exitDistributionKnownExpression(
            final CountlangParser.DistributionKnownExpressionContext ctx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleExpressionExit(delegationCtx);
    }

    abstract void addExpression(final ExpressionNode expression);
}
