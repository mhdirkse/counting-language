package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

public class DistributionExpressionWithTotal extends AbstractDistributionExpression {
    private ExpressionNode totalExpression;

    public DistributionExpressionWithTotal(int line, int column) {
        super(line, column);
    }

    public void setTotalExpression(ExpressionNode totalExpression) {
        this.totalExpression = totalExpression;
    }

    @Override
    public List<AstNode> getChildren() {
        List<AstNode> result = new ArrayList<>();
        result.add(totalExpression);
        result.addAll(super.getChildren());
        return result;
    }

    @Override
    public int getNumSubExpressions() {
        return super.getNumSubExpressions() + 1;
    }

    @Override
    public void accept(Visitor v) {
        v.visitDistributionExpressionWithTotal(this);
    }
}
