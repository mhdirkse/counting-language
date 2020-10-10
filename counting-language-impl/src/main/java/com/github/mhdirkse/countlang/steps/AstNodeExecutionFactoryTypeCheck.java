package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithTotal;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithUnknown;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FormalParameters;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.IfStatement;
import com.github.mhdirkse.countlang.ast.MarkUsedStatement;
import com.github.mhdirkse.countlang.ast.Operator;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.ast.ReturnStatement;
import com.github.mhdirkse.countlang.ast.SimpleDistributionExpression;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.ast.Visitor;

class AstNodeExecutionFactoryTypeCheck implements AstNodeExecutionFactory<CountlangType>, Visitor {
    private AstNodeExecution<CountlangType> result;

    @Override
    public AstNodeExecution<CountlangType> create(AstNode node) {
        node.accept(this);
        return result;
    }

    @Override
    public void visitStatementGroup(StatementGroup statementGroup) {
        result = new StatementGroupHandler.Analysis(statementGroup);
    }

    @Override
    public void visitAssignmentStatement(AssignmentStatement statement) {
        result = new AssignmentStatementHandler<CountlangType>(statement);
    }

    @Override
    public void visitPrintStatement(PrintStatement statement) {
        result = new SimpleStatementTypeCheck(statement);
    }

    @Override
    public void visitMarkUsedStatement(MarkUsedStatement statement) {
        result = new SimpleStatementTypeCheck(statement);
    }

    @Override
    public void visitFunctionDefinitionStatement(FunctionDefinitionStatement statement) {
        result = new FunctionDefinitionStatementTypeCheck(statement);
    }

    @Override
    public void visitReturnStatement(ReturnStatement statement) {
        result = new ReturnStatementHandler.Analysis(statement);
    }

    @Override
    public void visitIfStatement(IfStatement ifStatement) {
        result = new IfStatementTypeCheck(ifStatement);
    }

    @Override
    public void visitCompositeExpression(CompositeExpression expression) {
        result = new CompositeExpressionTypeCheck(expression);
    }

    @Override
    public void visitFunctionCallExpression(FunctionCallExpression expression) {
        result = new FunctionCallExpressionTypeCheck(expression);
    }

    @Override
    public void visitSymbolExpression(SymbolExpression expression) {
        result = new SymbolExpressionTypeCheck(expression);
    }

    @Override
    public void visitValueExpression(ValueExpression expression) {
        result = new ValueExpressionTypeCheck(expression);
    }

    @Override
    public void visitOperator(Operator operator) {
    }

    @Override
    public void visitFormalParameters(FormalParameters formalParameters) {
    }

    @Override
    public void visitFormalParameter(FormalParameter formalParameter) {
    }

    @Override
    public void visitSimpleDistributionExpression(SimpleDistributionExpression expr) {
        result = new DistributionExpressionTypeCheck(expr);
    }

    @Override
    public void visitDistributionExpressionWithTotal(DistributionExpressionWithTotal expr) {
        result = new DistributionExpressionTypeCheck(expr);
    }

    @Override
    public void visitDistributionExpressionWithUnknown(DistributionExpressionWithUnknown expr) {
        result = new DistributionExpressionTypeCheck(expr);
    }
}
