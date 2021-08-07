package com.github.mhdirkse.countlang.ast;

import com.github.mhdirkse.countlang.type.CountlangType;

public class ArrayTypeNode extends CompositeTypeNode {
    public ArrayTypeNode(int line, int column) {
        super(line, column);
    }

    @Override
    public CountlangType getCountlangType() {
        return CountlangType.arrayOf(children.get(0).getCountlangType());
    }

    @Override
    public void accept(Visitor v) {
        v.visitArrayTypeNode(this);
    }
}
