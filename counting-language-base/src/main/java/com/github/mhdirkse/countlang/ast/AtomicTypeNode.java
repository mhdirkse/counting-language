package com.github.mhdirkse.countlang.ast;

import com.github.mhdirkse.countlang.type.CountlangType;

public class AtomicTypeNode extends TypeNode {
    private final CountlangType countlangType;

    public AtomicTypeNode(int line, int column, CountlangType theCountlangType) {
        super(line, column);
        this.countlangType = theCountlangType;
    }

    @Override
    public CountlangType getCountlangType() {
        return countlangType;
    }

    @Override
    public void accept(Visitor v) {
        v.visitAtomicTypeNode(this);
    }
}
