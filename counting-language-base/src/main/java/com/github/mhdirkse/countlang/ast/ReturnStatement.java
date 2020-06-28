package com.github.mhdirkse.countlang.ast;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class ReturnStatement extends Statement implements CompositeNode {
    @Getter
    @Setter
    private ExpressionNode expression = null;

    public ReturnStatement(final int line, final int column) {
        super(line, column);
    }

    @Override
    public void accept(final Visitor v) {
        v.visitReturnStatement(this);
    }

    @Override
    public List<AstNode> getChildren() {
        return Arrays.asList(expression);
    }
}
