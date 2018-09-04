package com.github.mhdirkse.countlang.ast;

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
