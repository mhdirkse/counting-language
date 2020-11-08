package com.github.mhdirkse.countlang.ast;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class SampleStatement extends Statement implements CompositeNode {
    @Getter
    @Setter
    private String symbol;

    @Getter
    @Setter
    private ExpressionNode sampledDistribution;

    public SampleStatement(int line, int column) {
        super(line, column);
    }

    @Override
    public void accept(Visitor v) {
        v.visitSampleStatement(this);
    }

    @Override
    public List<AstNode> getChildren() {
        return Arrays.asList(sampledDistribution);
    }
}
