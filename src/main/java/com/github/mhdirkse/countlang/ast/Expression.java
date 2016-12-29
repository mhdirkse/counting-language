package com.github.mhdirkse.countlang.ast;

public abstract class Expression extends AstNode {
    public abstract Value calculate(final ExecutionContext ctx);
}
