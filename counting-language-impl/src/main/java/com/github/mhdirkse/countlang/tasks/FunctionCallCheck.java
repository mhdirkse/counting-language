package com.github.mhdirkse.countlang.tasks;

import java.util.HashMap;
import java.util.Map;

import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.AstListener;
import com.github.mhdirkse.countlang.ast.AstVisitorToListener;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FormalParameters;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.Operator;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.ast.Program;
import com.github.mhdirkse.countlang.ast.ReturnStatement;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.TestFunctionDefinitions;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.ast.Visitor;

class FunctionCallCheck implements AstListener {
    private final Map<String, Integer> argumentCounts;
    private final StatusReporter reporter;

    FunctionCallCheck(final StatusReporter reporter) {
        this.reporter = reporter;
        this.argumentCounts = new HashMap<>();
        FunctionDefinitionStatement testFunction = TestFunctionDefinitions.createTestFunction();
        argumentCounts.put(testFunction.getName(), testFunction.getNumParameters());
    }

    void run(final Program program) {
        Visitor v = new AstVisitorToListener(this);
        program.accept(v);
    }
    
    @Override
    public void enterProgram(Program p1) {
    }

    @Override
    public void exitProgram(Program p1) {
    }

    @Override
    public void enterAssignmentStatement(AssignmentStatement p1) {
    }

    @Override
    public void exitAssignmentStatement(AssignmentStatement p1) {
    }

    @Override
    public void enterPrintStatement(PrintStatement p1) {
    }

    @Override
    public void exitPrintStatement(PrintStatement p1) {
    }

    @Override
    public void enterFunctionDefinitionStatement(FunctionDefinitionStatement statement) {
        if(argumentCounts.containsKey(statement.getName())) {
            reporter.report(
                    StatusCode.FUNCTION_ALREADY_DEFINED,
                    statement.getLine(),
                    statement.getColumn(), statement.getName());
        } else {
            argumentCounts.put(statement.getName(), statement.getNumParameters());
        }
    }

    @Override
    public void exitFunctionDefinitionStatement(FunctionDefinitionStatement p1) {
    }

    @Override
    public void enterReturnStatement(ReturnStatement p1) {
    }

    @Override
    public void exitReturnStatement(ReturnStatement p1) {
    }

    @Override
    public void enterCompositeExpression(CompositeExpression p1) {
    }

    @Override
    public void exitCompositeExpression(CompositeExpression p1) {
    }

    @Override
    public void visitOperator(Operator p1) {
    }

    @Override
    public void enterFunctionCallExpression(FunctionCallExpression expression) {
        if(!argumentCounts.containsKey(expression.getFunctionName())) {
            reporter.report(
                    StatusCode.FUNCTION_DOES_NOT_EXIST,
                    expression.getLine(),
                    expression.getColumn(),
                    expression.getFunctionName());
        } else if(!argumentCounts.get(expression.getFunctionName()).equals(expression.getNumArguments())) {
            reporter.report(
                    StatusCode.FUNCTION_ARGUMENT_COUNT_MISMATCH,
                    expression.getLine(),
                    expression.getColumn(),
                    expression.getFunctionName(),
                    argumentCounts.get(expression.getFunctionName()).toString(),
                    Integer.valueOf(expression.getNumArguments()).toString());
        }
    }

    @Override
    public void exitFunctionCallExpression(FunctionCallExpression p1) {
    }

    @Override
    public void visitSymbolExpression(SymbolExpression p1) {
    }

    @Override
    public void visitValueExpression(ValueExpression p1) {
    }

    @Override
    public void enterFormalParameters(FormalParameters p1) {
    }

    @Override
    public void exitFormalParameters(FormalParameters p1) {
    }

    @Override
    public void visitFormalParameter(FormalParameter p1) {
    }
}
