package com.github.mhdirkse.countlang.ast;

import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.Expression;
import com.github.mhdirkse.countlang.execution.Value;

public abstract class ExpressionNode extends AstNode implements Expression {
    public ExpressionNode(final int line, final int column) {
        super(line, column);
    }

    @Override
    public abstract Value calculate(final ExecutionContext ctx);
}
