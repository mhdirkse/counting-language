package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.ExpressionResultCollectorVarUsage.ResultStrategy.NO_RESULT;
import static com.github.mhdirkse.countlang.steps.ExpressionResultCollectorVarUsage.ResultStrategy.RESULT;

import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithTotal;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithUnknown;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.IfStatement;
import com.github.mhdirkse.countlang.ast.MarkUsedStatement;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.ast.ReturnStatement;
import com.github.mhdirkse.countlang.ast.SimpleDistributionExpression;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.execution.DummyValue;

class AstNodeExecutionFactoryVarUsage extends AbstractAstNodeExecutionFactory<DummyValue> {
    @Override
    public void visitStatementGroup(StatementGroup statementGroup) {
        result = new StatementGroupHandler.VarUsage(statementGroup);
    }

    @Override
    public void visitAssignmentStatement(AssignmentStatement statement) {
        result = new AssignmentStatementHandler<DummyValue>(statement);
    }

    @Override
    public void visitPrintStatement(PrintStatement statement) {
        result = new ExpressionResultCollectorVarUsage(statement, NO_RESULT);
    }

    @Override
    public void visitMarkUsedStatement(MarkUsedStatement statement) {
        result = new ExpressionResultCollectorVarUsage(statement, NO_RESULT);
    }

    @Override
    public void visitFunctionDefinitionStatement(FunctionDefinitionStatement statement) {
        result = new FunctionDefinitionStatementAnalysis.VarUsage(statement);
    }

    @Override
    public void visitReturnStatement(ReturnStatement statement) {
        result = new ExpressionResultCollectorVarUsage(statement, RESULT);        
    }

    @Override
    public void visitIfStatement(IfStatement ifStatement) {
        result = new IfStatementAnalysis.VarUsage(ifStatement);
    }

    @Override
    public void visitCompositeExpression(CompositeExpression expression) {
        result = new ExpressionResultCollectorVarUsage(expression, RESULT);        
    }

    @Override
    public void visitFunctionCallExpression(FunctionCallExpression expression) {
        result = new ExpressionResultCollectorVarUsage(expression, RESULT);        
    }

    @Override
    public void visitSymbolExpression(SymbolExpression expression) {
        result = new SymbolExpressionVarUsage(expression);
    }

    @Override
    public void visitValueExpression(ValueExpression expression) {
        result = new ValueExpressionVarUsage(expression);
    }

    @Override
    public void visitSimpleDistributionExpression(SimpleDistributionExpression expr) {
        result = new ExpressionResultCollectorVarUsage(expr, RESULT);        
    }

    @Override
    public void visitDistributionExpressionWithTotal(DistributionExpressionWithTotal expr) {
        result = new ExpressionResultCollectorVarUsage(expr, RESULT);        
    }

    @Override
    public void visitDistributionExpressionWithUnknown(DistributionExpressionWithUnknown expr) {
        result = new ExpressionResultCollectorVarUsage(expr, RESULT);        
    }
}
