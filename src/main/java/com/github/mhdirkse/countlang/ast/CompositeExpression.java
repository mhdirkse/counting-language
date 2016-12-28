package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

public final class CompositeExpression extends Expression {
    private Operator operator = null;
    private List<Expression> subExpressions = new ArrayList<Expression>();

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(final Operator operator) {
        this.operator = operator;
    }

    public int getNumSubExpressions() {
        return subExpressions.size();
    }

    public Expression getSubExpression(final int index) {
        return subExpressions.get(index);
    }

    public void addSubExpression(final Expression expression) {
        subExpressions.add(expression);
    }

    @Override
    public Value calculate(final ExecutionContext ctx) {
        List<Value> arguments = new ArrayList<Value>();
        for (Expression subExpression : subExpressions) {
            arguments.add(subExpression.calculate(ctx));
        }
        return operator.execute(arguments);
    }
}
