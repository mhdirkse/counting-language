package com.github.mhdirkse.countlang.ast;

import com.github.mhdirkse.countlang.execution.CountlangType;

import lombok.Getter;

public class FormalParameter extends AstNode {
    @Getter
    private final String name;

    @Getter
    private final CountlangType countlangType;

    public FormalParameter(final int line, final int column, final String name, CountlangType countlangType) {
        super(line, column);
        this.name = name;
        this.countlangType = countlangType;
    }

    @Override
    public void accept(final Visitor v) {
        v.visitFormalParameter(this);
    }
}
