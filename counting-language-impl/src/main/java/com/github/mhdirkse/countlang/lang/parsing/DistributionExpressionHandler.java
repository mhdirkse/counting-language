package com.github.mhdirkse.countlang.lang.parsing;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.AbstractDistributionExpression;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithTotal;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithUnknown;
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

    private List<ExpressionNode> scoredExpressions = new ArrayList<>();
    private ExpressionNode extraExpression;
    
    public DistributionExpressionHandler(final int line, final int column) {
        this.line = line;
        this.column = column;
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
    void addExpression(ExpressionNode expression) {
        switch(kind) {
        case DEFAULT:
            scoredExpressions.add(expression);
            break;
        case WITH_TOTAL:
        case WITH_UNKNOWN:
            extraExpression = expression;
            break;
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
        for(ExpressionNode se: scoredExpressions) {
            distributionExpression.addScoredValue(se);
        }
        return distributionExpression;
    }
}
