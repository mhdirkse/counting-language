package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public final class CompositeExpression extends ExpressionNode implements CompositeNode {
    private Operator operator = null;
    private List<ExpressionNode> subExpressions = new ArrayList<>();

    @Getter
    @Setter
    private CountlangType countlangType;

    public CompositeExpression(final int line, final int column) {
        super(line, column);
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(final Operator operator) {
        this.operator = operator;
    }

    public int getNumSubExpressions() {
        return subExpressions.size();
    }

    public ExpressionNode getSubExpression(final int index) {
        return subExpressions.get(index);
    }

    public void addSubExpression(final ExpressionNode expression) {
        subExpressions.add(expression);
    }

    @Override
    public void accept(final Visitor v) {
        v.visitCompositeExpression(this);
    }

    @Override
    public List<AstNode> getChildren() {
        List<AstNode> result = new ArrayList<>();
        result.add(operator);
        result.addAll(subExpressions);
        return result;
    }
}
