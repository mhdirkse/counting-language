package com.github.mhdirkse.countlang.ast;

import java.util.Arrays;
import java.util.List;

public final class AssignmentStatement extends Statement implements CompositeNode {
    private String lhs = null;
    private ExpressionNode rhs = null;

    public AssignmentStatement(final int line, final int column) {
        super(line, column);
    }

    public String getLhs() {
        return lhs;
    }

    public void setLhs(final String lhs) {
        this.lhs = lhs;
    }

    public ExpressionNode getRhs() {
        return rhs;
    }

    public void setRhs(final ExpressionNode rhs) {
        this.rhs = rhs;
    }

    @Override
    public void accept(final Visitor v) {
        v.visitAssignmentStatement(this);
    }

    @Override
    public List<AstNode> getChildren() {
        return Arrays.asList(rhs);
    }
}
