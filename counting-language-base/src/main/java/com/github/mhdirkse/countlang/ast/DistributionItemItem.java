package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DistributionItemItem extends AbstractDistributionItem {
    private ExpressionNode item;

    public DistributionItemItem(int line, int column) {
        super(line, column);
    }

    @Override
    public ExpressionNode getItem() {
        return item;
    }

    @Override
    public void setItem(ExpressionNode item) {
        this.item = item;
    }

    @Override
    public void accept(Visitor v) {
        v.visitDistributionItemItem(this);
    }

    @Override
    public List<AstNode> getChildren() {
        return new ArrayList<>(Arrays.asList(item));
    }
}
