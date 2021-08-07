package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.mhdirkse.countlang.type.CountlangType;

public class ArrayExpression extends ExpressionNode implements CompositeNode {
    public ArrayExpression(int line, int column) {
        super(line, column);
    }

    private CountlangType countlangType = CountlangType.unknown();
    private List<ExpressionNode> elements = new ArrayList<>();

    @Override
    public CountlangType getCountlangType() {
        return countlangType;
    }

    public void setCountlangType(CountlangType countlangType) {
        this.countlangType = countlangType;
    }

    public void addElement(ExpressionNode element) {
        elements.add(element);
    }

    @Override
    public List<AstNode> getChildren() {
        return elements.stream().map(e -> (AstNode) e).collect(Collectors.toList());
    }

    @Override
    public void accept(Visitor v) {
        v.visitArrayExpression(this);
    }
}
