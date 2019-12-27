package com.github.mhdirkse.countlang.ast;

import com.github.mhdirkse.countlang.execution.CountlangType;

import lombok.Getter;

public class FormalParameter extends AstNode {
    @Getter
    private String name;

    @Getter
    private CountlangType countlangType = CountlangType.INT;

    public FormalParameter(final int line, final int column, final String name) {
        super(line, column);
        this.name = name;
    }

    @Override
    public void accept(final Visitor v) {
        v.visitFormalParameter(this);
    }
}
