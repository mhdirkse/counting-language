package com.github.mhdirkse.countlang.ast;

import java.util.Arrays;
import java.util.List;

import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.ProgramRuntimeException;

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
    public void execute(final ExecutionContext ctx) {
        throw new ProgramRuntimeException(getLine(), getColumn(), "Return statemnt outside function");
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
