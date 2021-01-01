package com.github.mhdirkse.countlang.analysis;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithTotal;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithUnknown;
import com.github.mhdirkse.countlang.ast.DistributionItemCount;
import com.github.mhdirkse.countlang.ast.DistributionItemItem;
import com.github.mhdirkse.countlang.ast.ExperimentDefinitionStatement;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FormalParameters;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatementBase;
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
import com.github.mhdirkse.countlang.execution.StackFrameAccess;
import com.github.mhdirkse.countlang.tasks.StatusReporter;

public class CountlangAnalysis implements Visitor {
    private FunctionDefinitions funDefs = new FunctionDefinitions();
    private CodeUnits codeUnits = new CodeUnits();
    private Memory memory = new Memory();
    private StatusReporter reporter;
    private int distributionItemIndex = 0;

    CountlangAnalysis(StatusReporter reporter) {
        this.reporter = reporter;
    }

    public void run(AstNode program) {
        program.accept(this);
        memory.report(reporter);
    }

    @Override
    public void visitStatementGroup(StatementGroup statementGroup) {
        memory.pushScope(StackFrameAccess.SHOW_PARENT);
        statementGroup.getChildren().forEach(this::handleStatementGroupChild);
        memory.popScope();
    }

    private void handleStatementGroupChild(AstNode node) {
        codeUnits.onEvent(ControlEvent.controlEvent(ControlEvent.Type.STATEMENT_OPEN, node.getLine(), node.getColumn()));
        node.accept(this);
        codeUnits.onEvent(ControlEvent.controlEvent(ControlEvent.Type.STATEMENT_CLOSE, node.getLine(), node.getColumn()));
    }

    @Override
    public void visitAssignmentStatement(AssignmentStatement statement) {
        statement.getRhs().accept(this);
        memory.write(statement.getLhs(), statement.getRhs().getCountlangType(), statement.getLine(), statement.getColumn());
    }

    @Override
    public void visitSampleStatement(SampleStatement statement) {
        statement.getSampledDistribution().accept(this);
        CountlangType sampledType = statement.getSampledDistribution().getCountlangType();
        if(sampledType != CountlangType.DISTRIBUTION) {
            // TODO: issue error
        }
        memory.write(statement.getSymbol(), CountlangType.INT, statement.getLine(), statement.getColumn());
    }

    @Override
    public void visitPrintStatement(PrintStatement statement) {
        statement.getExpression().accept(this);
    }

    @Override
    public void visitMarkUsedStatement(MarkUsedStatement statement) {
        statement.getExpression().accept(this);
    }

    @Override
    public void visitFunctionDefinitionStatement(FunctionDefinitionStatement statement) {
        if(codeUnits.checkFunctionOrExperimentDefinitionAllowed(reporter)) {
            codeUnits.push(new CodeUnitFunction(statement));
            continueFunctionOrExperiment(statement);
        }
    }

    private void continueFunctionOrExperiment(FunctionDefinitionStatementBase statement) {
        memory.pushScope(StackFrameAccess.HIDE_PARENT);
        statement.getFormalParameters().getFormalParameters().forEach(p -> p.accept(this));
        statement.getStatements().accept(this);
        memory.popScope();
        codeUnits.pop();
    }

    @Override
    public void visitFormalParameters(FormalParameters formalParameters) {
    }

    @Override
    public void visitFormalParameter(FormalParameter p) {
        memory.define(p.getName(), p.getCountlangType(), p.getLine(), p.getColumn());
    }

    @Override
    public void visitExperimentDefinitionStatement(ExperimentDefinitionStatement statement) {
        if(codeUnits.checkFunctionOrExperimentDefinitionAllowed(reporter)) {
            codeUnits.push(new CodeUnitExperiment(statement));
            continueFunctionOrExperiment(statement);
        }
    }

    @Override
    public void visitReturnStatement(ReturnStatement statement) {
        statement.getExpression().accept(this);
        ControlEvent returnEvent = ControlEvent.returnEvent(statement.getExpression().getCountlangType(), statement.getLine(), statement.getColumn());
        codeUnits.onEvent(returnEvent);
    }

    @Override
    public void visitIfStatement(IfStatement ifStatement) {
        ifStatement.getSelector().accept(this);
        if(ifStatement.getSelector().getCountlangType() != CountlangType.BOOL) {
            // TODO: Error selector not boolean
        }
        issueEvent(ControlEvent.controlEvent(ControlEvent.Type.SWITCH_OPEN, ifStatement.getLine(), ifStatement.getColumn()));
        issueEvent(ControlEvent.controlEvent(ControlEvent.Type.BRANCH_OPEN, ifStatement.getThenStatement().getLine(), ifStatement.getThenStatement().getColumn()));
        ifStatement.getThenStatement().accept(this);
        issueEvent(ControlEvent.controlEvent(ControlEvent.Type.BRANCH_CLOSE, ifStatement.getThenStatement().getLine(), ifStatement.getThenStatement().getColumn()));
        if(ifStatement.getElseStatement() == null) {
            issueEvent(ControlEvent.controlEvent(ControlEvent.Type.BRANCH_OPEN, ifStatement.getLine(), ifStatement.getColumn()));
            issueEvent(ControlEvent.controlEvent(ControlEvent.Type.BRANCH_CLOSE, ifStatement.getLine(), ifStatement.getColumn()));
        } else {
            issueEvent(ControlEvent.controlEvent(ControlEvent.Type.BRANCH_OPEN, ifStatement.getElseStatement().getLine(), ifStatement.getElseStatement().getColumn()));
            ifStatement.getElseStatement().accept(this);
            issueEvent(ControlEvent.controlEvent(ControlEvent.Type.BRANCH_CLOSE, ifStatement.getElseStatement().getLine(), ifStatement.getElseStatement().getColumn()));
        }
        issueEvent(ControlEvent.controlEvent(ControlEvent.Type.SWITCH_CLOSE, ifStatement.getLine(), ifStatement.getColumn()));
    }

    private void issueEvent(ControlEvent ev) {
        memory.onControlEvent(ev);
        codeUnits.onEvent(ev);
    }

    @Override
    public void visitWhileStatement(WhileStatement whileStatement) {
        issueEvent(ControlEvent.controlEvent(ControlEvent.Type.REPETITION_START, whileStatement.getLine(), whileStatement.getColumn()));
        whileStatement.getTestExpr().accept(this);
        if(whileStatement.getTestExpr().getCountlangType() != CountlangType.BOOL) {
            // TODO: Error test condition not Boolean
        }
        whileStatement.getStatement().accept(this);
        issueEvent(ControlEvent.controlEvent(ControlEvent.Type.REPETITION_STOP, whileStatement.getLine(), whileStatement.getColumn()));
    }

    @Override
    public void visitCompositeExpression(CompositeExpression expression) {
        List<CountlangType> argTypes = new ArrayList<>();
        for(ExpressionNode subExpr: expression.getSubExpressions()) {
            subExpr.accept(this);
            argTypes.add(subExpr.getCountlangType());
        }
        expression.getOperator().checkAndEstablishTypes(argTypes);
        expression.setCountlangType(expression.getOperator().getResultType());
    }

    @Override
    public void visitFunctionCallExpression(FunctionCallExpression expression) {
        expression.getSubExpressions().forEach(expr -> expr.accept(this));
        expression.setCountlangType(CountlangType.UNKNOWN);
        if(! funDefs.hasFunction(expression.getFunctionName())) {
            // TODO: Error function does not exist
            return;
        }
        FunctionDefinitionStatementBase fun = funDefs.getFunction(expression.getFunctionName());
        expression.setCountlangType(fun.getReturnType());
        if(expression.getNumArguments() != fun.getNumParameters()) {
            // TODO: Error incorrect number of parameters
            return;
        }
        for(int i = 0; i < expression.getNumArguments(); i++) {
            if(expression.getArgument(i).getCountlangType() != fun.getFormalParameterType(i)) {
                // TODO: Error incorrect parameter type
            }
        }
    }

    @Override
    public void visitSymbolExpression(SymbolExpression expression) {
        expression.setCountlangType(memory.read(expression.getSymbol(), expression.getLine(), expression.getColumn()));
    }

    @Override
    public void visitValueExpression(ValueExpression expression) {
    }

    @Override
    public void visitOperator(Operator operator) {
    }

    @Override
    public void visitSimpleDistributionExpression(SimpleDistributionExpression expr) {
        for(distributionItemIndex = 0; distributionItemIndex < expr.getChildren().size(); distributionItemIndex++) {
            expr.getChildren().get(distributionItemIndex).accept(this);
        }
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
