package com.github.mhdirkse.countlang.ast;

public final class AssignmentStatement extends Statement {
    private Symbol lhs = null;
    private Expression rhs = null;

    public Symbol getLhs() {
        return lhs;
    }

    public void setLhs(final Symbol lhs) {
        this.lhs = lhs;
    }

    public Expression getRhs() {
        return rhs;
    }

    public void setRhs(final Expression rhs) {
        this.rhs = rhs;
    }

    @Override
    public void execute(final ExecutionContext ctx) {
        Scope scope = ctx.getScope();
        scope.putSymbol(lhs.getName(), rhs.calculate(ctx));
    }
}
