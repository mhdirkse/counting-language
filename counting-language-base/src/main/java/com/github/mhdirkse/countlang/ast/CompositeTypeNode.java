package com.github.mhdirkse.countlang.ast;

import java.util.ArrayList;
import java.util.List;

public abstract class CompositeTypeNode extends TypeNode implements CompositeNode {
    List<TypeNode> children = new ArrayList<>();

    public void addChild(TypeNode child) {
        children.add(child);
    }

    public CompositeTypeNode(int line, int column) {
        super(line, column);
    }

    @Override
    public List<AstNode> getChildren() {
        return new ArrayList<>(children);
    }
}
