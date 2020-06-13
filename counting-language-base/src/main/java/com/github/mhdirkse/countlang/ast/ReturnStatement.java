package com.github.mhdirkse.countlang.ast;

import java.util.Arrays;
import java.util.List;

import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.ProgramException;
import com.github.mhdirkse.countlang.execution.ReturnHandler;

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
    public void execute(final ExecutionContext ctx, final ReturnHandler returnHandler) {
        Object returnValue = expression.calculate(ctx);
        if(returnValue == null) {
            throw new ProgramException(
                    getLine(), getColumn(), "Cannot return null");
        }
        returnHandler.handleReturnValue(returnValue, getLine(), getColumn());
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
