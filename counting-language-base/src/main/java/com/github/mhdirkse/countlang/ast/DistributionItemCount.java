package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

public class DistributionItemCount extends AbstractDistributionItem {
    private ExpressionNode count;
    private ExpressionNode item;

    public DistributionItemCount(int line, int column) {
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

    public ExpressionNode getCount() {
        return count;
    }

    public void setCount(ExpressionNode count) {
        this.count = count;
    }

    @Override
    public void accept(Visitor v) {
        v.visitDistributionItemCount(this);
    }

    @Override
    public List<AstNode> getChildren() {
        List<AstNode> result = new ArrayList<>();
        result.add(count);
        result.add(item);
        return result;
    }
}
