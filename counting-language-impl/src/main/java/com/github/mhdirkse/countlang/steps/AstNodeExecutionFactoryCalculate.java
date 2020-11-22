package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithTotal;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithUnknown;
import com.github.mhdirkse.countlang.ast.ExperimentDefinitionStatement;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.IfStatement;
import com.github.mhdirkse.countlang.ast.MarkUsedStatement;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.ast.ReturnStatement;
import com.github.mhdirkse.countlang.ast.SampleStatement;
import com.github.mhdirkse.countlang.ast.SimpleDistributionExpression;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.ValueExpression;

class AstNodeExecutionFactoryCalculate extends AbstractAstNodeExecutionFactory {
    @Override
    public void visitStatementGroup(StatementGroup statementGroup) {
        result = new StatementGroupCalculation(statementGroup);
    }

    @Override
    public void visitAssignmentStatement(AssignmentStatement statement) {
        result = new AssignmentStatementCalculation(statement);
    }

    @Override
    public void visitPrintStatement(PrintStatement statement) {
        result = new PrintStatementCalculation(statement);
    }

    @Override
    public void visitMarkUsedStatement(MarkUsedStatement statement) {
        result = new MarkUsedStatementCalculation(statement);
    }

    @Override
    public void visitFunctionDefinitionStatement(FunctionDefinitionStatement statement) {
        result = new FunctionDefinitionStatementBaseCalculation(statement);
    }

    @Override
    public void visitReturnStatement(ReturnStatement statement) {
        result = new ReturnStatementCalculation(statement);
    }

    @Override
    public void visitIfStatement(IfStatement ifStatement) {
        result = new IfStatementCalculation(ifStatement);
    }

    @Override
    public void visitCompositeExpression(CompositeExpression expression) {
        result = new CompositeExpressionCalculation(expression);
    }

    @Override
    public void visitFunctionCallExpression(FunctionCallExpression expression) {
        result = new FunctionCallExpressionCalculation(expression);
    }

    @Override
    public void visitSymbolExpression(SymbolExpression expression) {
        result = new SymbolExpressionCalculation(expression);
    }

    @Override
    public void visitValueExpression(ValueExpression expression) {
        result = new ValueExpressionCalculation(expression);
    }

    @Override
    public void visitSimpleDistributionExpression(SimpleDistributionExpression expr) {
        result = new SimpleDistributionExpressionCalculation(expr);
    }

    @Override
    public void visitDistributionExpressionWithTotal(DistributionExpressionWithTotal expr) {
        result = new SpecialDistributionExpressionCalculation.WithTotal(expr);
    }

    @Override
    public void visitDistributionExpressionWithUnknown(DistributionExpressionWithUnknown expr) {
        result = new SpecialDistributionExpressionCalculation.WithUnknown(expr);
    }

    @Override
    public void visitSampleStatement(SampleStatement statement) {
        result = new SampleStatementCalculation(statement);
    }

    @Override
    public void visitExperimentDefinitionStatement(ExperimentDefinitionStatement statement) {
        result = new FunctionDefinitionStatementBaseCalculation(statement);
    }
}
