package com.github.mhdirkse.countlang.tasks;

import java.util.HashMap;
import java.util.Map;

import com.github.mhdirkse.countlang.ast.AbstractAstListener;
import com.github.mhdirkse.countlang.ast.AstVisitorToListener;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.ast.TestFunctionDefinitions;
import com.github.mhdirkse.countlang.ast.Visitor;

class FunctionCallCheck extends AbstractAstListener {
    private final Map<String, Integer> argumentCounts;
    private final StatusReporter reporter;

    FunctionCallCheck(final StatusReporter reporter) {
        this.reporter = reporter;
        this.argumentCounts = new HashMap<>();
        FunctionDefinitionStatement testFunction = TestFunctionDefinitions.createTestFunction();
        argumentCounts.put(testFunction.getName(), testFunction.getNumParameters());
    }

    void run(final StatementGroup statementGroup) {
        Visitor v = new AstVisitorToListener(this);
        statementGroup.accept(v);
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
}
