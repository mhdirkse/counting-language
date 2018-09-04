package com.github.mhdirkse.countlang.ast;

import java.util.Arrays;
import java.util.List;

import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.Symbol;

public final class AssignmentStatement extends Statement implements CompositeNode {
    private Symbol lhs = null;
    private ExpressionNode rhs = null;

    public AssignmentStatement(final int line, final int column) {
        super(line, column);
    }

    public Symbol getLhs() {
        return lhs;
    }

    public void setLhs(final Symbol lhs) {
        this.lhs = lhs;
    }

    public ExpressionNode getRhs() {
        return rhs;
    }

    public void setRhs(final ExpressionNode rhs) {
        this.rhs = rhs;
    }

    @Override
    public void execute(final ExecutionContext ctx) {
        ctx.putSymbol(lhs.getName(), rhs.calculate(ctx));
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
