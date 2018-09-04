package com.github.mhdirkse.countlang.ast;

public abstract class AstNode {
    private final int line;
    private final int column;

    public AstNode(final int line, final int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public abstract void accept(final Visitor v);
}
