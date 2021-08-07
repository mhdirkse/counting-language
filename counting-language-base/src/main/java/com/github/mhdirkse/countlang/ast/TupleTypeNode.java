package com.github.mhdirkse.countlang.ast;

import java.util.List;
import java.util.stream.Collectors;

import com.github.mhdirkse.countlang.type.CountlangType;

public class TupleTypeNode extends CompositeTypeNode {
    public TupleTypeNode(int line, int column) {
        super(line, column);
    }

    @Override
    public CountlangType getCountlangType() {
        List<CountlangType> childTypes = children.stream().map(TypeNode::getCountlangType).collect(Collectors.toList());
        return CountlangType.tupleOf(childTypes);
    }

    @Override
    public void accept(Visitor v) {
        v.visitTupleTypeNode(this);
    }
}
