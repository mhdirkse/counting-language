package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

public class DistributionExpressionWithUnknown extends AbstractDistributionExpression {
    private ExpressionNode unknownExpression;

    public DistributionExpressionWithUnknown(int line, int column) {
        super(line, column);
    }

    public void setUnknownExpression(ExpressionNode unknownExpression) {
        this.unknownExpression = unknownExpression;
    }

    @Override
    public List<AstNode> getChildren() {
        List<AstNode> result = new ArrayList<>();
        result.add(unknownExpression);
        result.addAll(super.getChildren());
        return result;
    }

    @Override
    public int getNumSubExpressions() {
        return super.getNumSubExpressions() + 1;
    }

    @Override
    public void accept(Visitor v) {
        v.visitDistributionExpressionWithUnknown(this);
    }
}
