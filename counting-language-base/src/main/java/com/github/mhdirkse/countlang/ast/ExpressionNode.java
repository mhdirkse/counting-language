package com.github.mhdirkse.countlang.ast;

import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.Expression;

public abstract class ExpressionNode extends AstNode implements Expression {
    public ExpressionNode(final int line, final int column) {
        super(line, column);
    }

    @Override
    public abstract Object calculate(final ExecutionContext ctx);
}
