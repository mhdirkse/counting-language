package com.github.mhdirkse.countlang.ast;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Setter;

public class TupleDealingLhs extends AbstractLhs implements CompositeNode {
    @Setter
    private List<AbstractTupleDealingLhsItem> children;

    public TupleDealingLhs(int line, int column) {
        super(line, column);
    }

    @Override
    public List<AstNode> getChildren() {
        return children.stream().map(c -> (AstNode) c).collect(Collectors.toList());
    }

    public int getNumChildren() {
    	return children.size();
    }

    @Override
    public void accept(Visitor v) {
        v.visitTupleDealingLhs(this);
    }
}
