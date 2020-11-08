package com.github.mhdirkse.countlang.ast;

public interface Visitor {
    public void visitStatementGroup(final StatementGroup statementGroup);
    public void visitAssignmentStatement(final AssignmentStatement statement);
    public void visitSampleStatement(final SampleStatement statement);
    public void visitPrintStatement(final PrintStatement statement);
    public void visitMarkUsedStatement(final MarkUsedStatement statement);
    public void visitFunctionDefinitionStatement(final FunctionDefinitionStatement statement);
    public void visitExperimentDefinitionStatement(final ExperimentDefinitionStatement statement);
    public void visitReturnStatement(final ReturnStatement statement);
    public void visitIfStatement(final IfStatement ifStatement);
    public void visitCompositeExpression(final CompositeExpression expression);
    public void visitFunctionCallExpression(final FunctionCallExpression expression);
    public void visitSymbolExpression(final SymbolExpression expression);
    public void visitValueExpression(final ValueExpression expression);
    public void visitOperator(final Operator operator);
    public void visitFormalParameters(final FormalParameters formalParameters);
    public void visitFormalParameter(final FormalParameter formalParameter);
    public void visitSimpleDistributionExpression(final SimpleDistributionExpression expr);
    public void visitDistributionExpressionWithTotal(final DistributionExpressionWithTotal expr);
    public void visitDistributionExpressionWithUnknown(final DistributionExpressionWithUnknown expr);
}
