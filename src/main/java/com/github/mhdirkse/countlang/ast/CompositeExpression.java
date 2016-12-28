package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

public final class CompositeExpression extends Expression {
    private Operator operator = null;
    private List<Expression> arguments = new ArrayList<Expression>();

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(final Operator operator) {
        this.operator = operator;
    }

    public int getNumArguments() {
        return arguments.size();
    }

    public Expression getArgument(final int index) {
        return arguments.get(index);
    }

    public void addArgument(final Expression expression) {
        arguments.add(expression);
    }
}
