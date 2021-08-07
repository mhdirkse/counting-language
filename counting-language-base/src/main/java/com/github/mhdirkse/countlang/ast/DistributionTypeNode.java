package com.github.mhdirkse.countlang.ast;

import com.github.mhdirkse.countlang.type.CountlangType;

public class DistributionTypeNode extends CompositeTypeNode {
    public DistributionTypeNode(int line, int column) {
        super(line, column);
    }

    @Override
    public CountlangType getCountlangType() {
        return CountlangType.distributionOf(children.get(0).getCountlangType());
    }

    @Override
    public void accept(Visitor v) {
        v.visitDistributionTypeNode(this);
    }
}
