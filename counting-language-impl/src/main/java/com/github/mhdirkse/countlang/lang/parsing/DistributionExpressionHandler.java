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

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.AbstractDistributionExpression;
import com.github.mhdirkse.countlang.ast.AbstractDistributionItem;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithTotal;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithUnknown;
import com.github.mhdirkse.countlang.ast.DistributionItemCount;
import com.github.mhdirkse.countlang.ast.DistributionItemItem;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.SimpleDistributionExpression;
import com.github.mhdirkse.countlang.lang.CountlangParser;

public class DistributionExpressionHandler extends AbstractExpressionHandler implements ExpressionSource {
    private static enum Kind {
        DEFAULT,
        WITH_TOTAL,
        WITH_UNKNOWN;
    }

    private Kind kind = Kind.DEFAULT;

    private int line;
    private int column;

    private List<AbstractDistributionItem> scoredExpressions = new ArrayList<>();
    private ExpressionNode extraExpression;
    private AbstractDistributionItem item;
    private TypeIdHandler typeIdHandler;

    public DistributionExpressionHandler(final int line, final int column) {
        this.line = line;
        this.column = column;
        this.item = null;
        typeIdHandler = new TypeIdHandler(line, column);
    }

    @Override
    public boolean enterDistItemCount(
            CountlangParser.DistItemCountContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        item = new DistributionItemCount(line, column);
        return true;
    }

    @Override
    public boolean exitDistItemCount(
            CountlangParser.DistItemCountContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        scoredExpressions.add(item);
        item = null;
        return true;
    }

    @Override
    public boolean enterDistItemSimple(
            CountlangParser.DistItemSimpleContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        item = new DistributionItemItem(line, column);
        return true;
    }

    @Override
    public boolean exitDistItemSimple(
            CountlangParser.DistItemSimpleContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        scoredExpressions.add(item);
        item = null;
        return true;
    }

    @Override
    public boolean visitTerminal (final TerminalNode terminalNode, final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        if(terminalNode.getSymbol().getType() == CountlangParser.TOTAL) {
            kind = Kind.WITH_TOTAL;
            return true;
        }
        if(terminalNode.getSymbol().getType() == CountlangParser.UNKNOWN) {
            kind = Kind.WITH_UNKNOWN;
            return true;
        }
        return false;
    }

    @Override
    public boolean enterSimpleType(
            CountlangParser.SimpleTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return typeIdHandler.enterSimpleType(antlrCtx, delegationCtx);
    }

    @Override
    public boolean enterDistributionType(
            CountlangParser.DistributionTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return typeIdHandler.enterDistributionType(antlrCtx, delegationCtx);
    }

    @Override
    public boolean exitSimpleType(
            CountlangParser.SimpleTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return typeIdHandler.exitSimpleType(antlrCtx, delegationCtx);
    }

    @Override
    public boolean exitDistributionType(
            CountlangParser.DistributionTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return typeIdHandler.exitDistributionType(antlrCtx, delegationCtx);
    }

    @Override
    public boolean enterArrayType(
            CountlangParser.ArrayTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return typeIdHandler.enterArrayType(antlrCtx, delegationCtx);
    }

    @Override
    public boolean exitArrayType(
            CountlangParser.ArrayTypeContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return typeIdHandler.exitArrayType(antlrCtx, delegationCtx);
    }

    @Override
    void addExpression(ExpressionNode expression) {
        switch(kind) {
        case DEFAULT:
            handleScoredExpression(expression);
            break;
        case WITH_TOTAL:
        case WITH_UNKNOWN:
            extraExpression = expression;
            break;
        }
    }

    private void handleScoredExpression(ExpressionNode expression) {
        if(item instanceof DistributionItemItem) {
            item.setItem(expression);
        } else {
            DistributionItemCount withCount = (DistributionItemCount) item;
            if(withCount.getCount() == null) {
                withCount.setCount(expression);
            } else {
                withCount.setItem(expression);
            }
        }
    }

    @Override
    public ExpressionNode getExpression() {
        AbstractDistributionExpression distributionExpression;
        switch(kind) {
        case DEFAULT:
            SimpleDistributionExpression simpleDistributionExpression = new SimpleDistributionExpression(line, column);
            distributionExpression = simpleDistributionExpression;
            break;
        case WITH_TOTAL:
            DistributionExpressionWithTotal distributionExpressionWithTotal = new DistributionExpressionWithTotal(line, column);
            distributionExpressionWithTotal.setTotalExpression(extraExpression);
            distributionExpression = distributionExpressionWithTotal;
            break;
        case WITH_UNKNOWN:
            DistributionExpressionWithUnknown distributionExpressionWithUnknown = new DistributionExpressionWithUnknown(line, column);
            distributionExpressionWithUnknown.setUnknownExpression(extraExpression);
            distributionExpression = distributionExpressionWithUnknown;
            break;
        default:
            throw new IllegalStateException("Cannot happen");
        }
        for(AbstractDistributionItem se: scoredExpressions) {
            distributionExpression.addScoredValue(se);
        }
        CountlangType countlangType = typeIdHandler.getCountlangType();
        if(countlangType != CountlangType.unknown()) {
            distributionExpression.setCountlangType(CountlangType.distributionOf(countlangType));
        }
        return distributionExpression;
    }
}
