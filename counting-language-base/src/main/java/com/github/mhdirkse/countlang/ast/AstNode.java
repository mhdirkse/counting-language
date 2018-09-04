package com.github.mhdirkse.countlang.ast;

public abstract class AstNode {
    public interface Visitor {
        public void visitProgram(final Program program);
        public void visitAssignmentStatement(final AssignmentStatement statement);
        public void visitPrintStatement(final PrintStatement statement);
        public void visitFunctionDefinitionStatement(final FunctionDefinitionStatement statement);
        public void visitReturnStatement(final ReturnStatement statement);
        public void visitCompositeExpression(final CompositeExpression expression);
        public void visitFunctionCallExpression(final FunctionCallExpression expression);
        public void visitSymbolExpression(final SymbolExpression expression);
        public void visitValueExpression(final ValueExpression expression);
        public void visitOperator(final Operator operator);
        public void visitFormalParameters(final FormalParameters formalParameters);
        public void visitFormalParameter(final FormalParameter formalParameter);
    }

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
