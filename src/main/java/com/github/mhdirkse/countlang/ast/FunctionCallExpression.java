package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

abstract public class FunctionCallExpression extends Expression {
    private String functionName = null;
    private List<Expression> arguments = new ArrayList<>();

    public FunctionCallExpression(final int line, final int column) {
        super(line, column);
    }

    @Override
    public void accept(final Visitor v) {
        v.visitFunctionCallExpression(this);
    }
}
