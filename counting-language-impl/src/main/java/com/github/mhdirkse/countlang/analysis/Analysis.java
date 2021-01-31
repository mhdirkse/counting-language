package com.github.mhdirkse.countlang.analysis;

import java.util.List;

import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithTotal;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithUnknown;
import com.github.mhdirkse.countlang.ast.DistributionItemCount;
import com.github.mhdirkse.countlang.ast.DistributionItemItem;
import com.github.mhdirkse.countlang.ast.ExperimentDefinitionStatement;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FormalParameters;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.IfStatement;
import com.github.mhdirkse.countlang.ast.MarkUsedStatement;
import com.github.mhdirkse.countlang.ast.Operator;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.ast.ReturnStatement;
import com.github.mhdirkse.countlang.ast.SampleStatement;
import com.github.mhdirkse.countlang.ast.SimpleDistributionExpression;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.ast.Visitor;
import com.github.mhdirkse.countlang.ast.WhileStatement;
import com.github.mhdirkse.countlang.execution.FunctionDefinitions;
import com.github.mhdirkse.countlang.tasks.SortingStatusReporter;
import com.github.mhdirkse.countlang.tasks.StatusReporter;

public class Analysis implements Visitor {
    private final FunctionDefinitions funDefs;
    private final SortingStatusReporter reporter;
    private final CodeBlocks codeBlocks;

    public Analysis(List<FunctionDefinitionStatement> funDefs) {
        this.funDefs = new FunctionDefinitions();
        funDefs.forEach(fds -> this.funDefs.putFunction(fds));
        reporter = new SortingStatusReporter();
        codeBlocks = new CodeBlocks(new MemoryImpl());
    }

    public void analyze(StatementGroup rootStatement, StatusReporter reporterDelegate) {
        rootStatement.accept(this);
        codeBlocks.getVariableErrorEvents().forEach(ev -> ev.report(reporter));
        // TODO: Also report errors related to returning.
        this.reporter.reportTo(reporterDelegate);
    }

    @Override
    public void visitStatementGroup(StatementGroup statementGroup) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitAssignmentStatement(AssignmentStatement statement) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitSampleStatement(SampleStatement statement) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitPrintStatement(PrintStatement statement) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitMarkUsedStatement(MarkUsedStatement statement) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitFunctionDefinitionStatement(FunctionDefinitionStatement statement) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitExperimentDefinitionStatement(ExperimentDefinitionStatement statement) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitReturnStatement(ReturnStatement statement) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitIfStatement(IfStatement ifStatement) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitWhileStatement(WhileStatement whileStatement) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitCompositeExpression(CompositeExpression expression) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitFunctionCallExpression(FunctionCallExpression expression) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitSymbolExpression(SymbolExpression expression) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitValueExpression(ValueExpression expression) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitOperator(Operator operator) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitFormalParameters(FormalParameters formalParameters) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitFormalParameter(FormalParameter formalParameter) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitSimpleDistributionExpression(SimpleDistributionExpression expr) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitDistributionExpressionWithTotal(DistributionExpressionWithTotal expr) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitDistributionExpressionWithUnknown(DistributionExpressionWithUnknown expr) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitDistributionItemItem(DistributionItemItem item) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitDistributionItemCount(DistributionItemCount item) {
        // TODO Auto-generated method stub
        
    }
}
