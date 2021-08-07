package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.type.CountlangType;

public class TupleExpression extends ExpressionNode implements CompositeNode {
    private CountlangType countlangType = CountlangType.unknown();
    private List<ExpressionNode> children = new ArrayList<>();

    public TupleExpression(int line, int column) {
        super(line, column);
    }

    @Override
    public CountlangType getCountlangType() {
        return countlangType;
    }

    public void setCountlangType(CountlangType countlangType) {
        this.countlangType = countlangType;
    }

    public void addChild(ExpressionNode child) {
        children.add(child);
    }

    @Override
    public List<AstNode> getChildren() {
        return new ArrayList<>(children);
    }

    @Override
    public void accept(Visitor v) {
        v.visitTupleExpression(this);
    }
}
