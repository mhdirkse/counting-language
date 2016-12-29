package com.github.mhdirkse.countlang.ast;

public abstract class AstNode {
    public interface Visitor {
        public void visitProgram(final Program program);
        public void visitAssignmentStatement(final AssignmentStatement statement);
        public void visitPrintStatement(final PrintStatement statement);
        public void visitCompositeExpression(final CompositeExpression expression);
        public void visitSymbolExpression(final SymbolExpression expression);
        public void visitValueExpression(final ValueExpression expression);
    }

    public abstract void accept(final Visitor v);
}
