package com.github.mhdirkse.countlang.ast;

import lombok.Getter;

public class FormalParameter extends AstNode {
    @Getter
    private String name;

    public FormalParameter(final int line, final int column, final String name) {
        super(line, column);
        this.name = name;
    }

    @Override
    public void accept(final AstNode.Visitor v) {
        v.visitFormalParameter(this);
    }
}
