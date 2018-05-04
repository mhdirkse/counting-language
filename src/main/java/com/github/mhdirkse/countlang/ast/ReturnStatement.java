package com.github.mhdirkse.countlang.ast;

import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.ProgramRuntimeException;

public class ReturnStatement extends Statement {
    private Expression expression = null;

    public ReturnStatement(final int line, final int column) {
        super(line, column);
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(final Expression expression) {
        this.expression = expression;
    }

    @Override
    public void execute(final ExecutionContext ctx) {
        throw new ProgramRuntimeException(getLine(), getColumn(), "Return statemnt outside function");
    }

    @Override
    public void accept(final AstNode.Visitor v) {
        v.visitReturnStatement(this);
    }
}
