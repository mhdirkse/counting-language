package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDistributionExpression extends ExpressionNode implements CompositeNode {
    private List<ExpressionNode> scoredValues = new ArrayList<>();

    public AbstractDistributionExpression(int line, int column) {
        super(line, column);
    }

    @Override
    public CountlangType getCountlangType() {
        return CountlangType.DISTRIBUTION;
    }

    public void addScoredValue(final ExpressionNode expression) {
        scoredValues.add(expression);
    }

    @Override
    public List<AstNode> getChildren() {
        List<AstNode> result = new ArrayList<>();
        result.addAll(scoredValues);
        return result;
    }

    public int getNumSubExpressions() {
        return scoredValues.size();
    }
}
