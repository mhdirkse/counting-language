package com.github.mhdirkse.countlang.ast;

import java.util.Arrays;
import java.util.List;

public class DereferenceExpression extends ExpressionNode implements CompositeNode {
    private CountlangType countlangType = CountlangType.unknown();
    private ExpressionNode container;
    private ExpressionNode reference;

    public DereferenceExpression(int line, int column) {
        super(line, column);
    }

    @Override
    public CountlangType getCountlangType() {
        return countlangType;
    }

    public void setCountlangType(CountlangType countlangType) {
        this.countlangType = countlangType;
    }

    public ExpressionNode getContainer() {
        return container;
    }

    public void setContainer(ExpressionNode container) {
        this.container = container;
    }

    public ExpressionNode getReference() {
        return reference;
    }

    public void setReference(ExpressionNode reference) {
        this.reference = reference;
    }

    @Override
    public List<AstNode> getChildren() {
        return Arrays.asList(container, reference);
    }

    @Override
    public void accept(Visitor v) {
        v.visitDereferenceExpression(this);
    }
}
