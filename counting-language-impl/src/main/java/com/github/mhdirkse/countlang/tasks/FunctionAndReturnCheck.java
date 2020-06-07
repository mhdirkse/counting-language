package com.github.mhdirkse.countlang.tasks;

import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FormalParameters;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.Operator;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.ast.ReturnStatement;
import com.github.mhdirkse.countlang.ast.Statement;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.ast.Visitor;

class FunctionAndReturnCheck {
    private final StatementGroup statementGroup;
    private final StatusReporter reporter;

    FunctionAndReturnCheck(final StatementGroup statementGroup, final StatusReporter reporter) {
        this.statementGroup = statementGroup;
        this.reporter = reporter;
    }

    void run() {
        statementGroup.getChildren().stream()
            .filter(c -> (c instanceof ReturnStatement))
            .forEach(c -> reporter.report(StatusCode.RETURN_OUTSIDE_FUNCTION, c.getLine(), c.getColumn()));
        statementGroup.getChildren().stream()
            .filter(c -> (c instanceof FunctionDefinitionStatement))
            .map(c -> (FunctionDefinitionStatement) c)
            .forEach(this::checkFunctionDefinitionStatement);
    }

    void checkFunctionDefinitionStatement(final FunctionDefinitionStatement fun) {
        StatementGroup statements = fun.getStatements();
        StatementHandler handler = new StatementHandler(fun.getName());
        statements.getChildren().forEach(c -> c.accept(handler));
        if(!handler.haveTheReturn) {
            reporter.report(
                    StatusCode.FUNCTION_DOES_NOT_RETURN, statements.getLine(), fun.getColumn(), fun.getName());
        }
    }

    private class StatementHandler implements Visitor {
        private final String functionName;
        boolean haveTheReturn = false;

        StatementHandler(final String functionName) {
            this.functionName = functionName;
        }

        @Override
        public void visitStatementGroup(StatementGroup statementGroup) {
        }

        @Override
        public void visitAssignmentStatement(AssignmentStatement statement) {
            checkBeforeReturn(statement);
        }

        public void checkBeforeReturn(final Statement statement) {
            if(haveTheReturn) {
                reporter.report(
                        StatusCode.FUNCTION_STATEMENT_WITHOUT_EFFECT,
                        statement.getLine(),
                        statement.getColumn(),
                        functionName);
            }
        }

        @Override
        public void visitPrintStatement(PrintStatement statement) {
            checkBeforeReturn(statement);
        }

        @Override
        public void visitFunctionDefinitionStatement(FunctionDefinitionStatement statement) {
            reporter.report(
                    StatusCode.FUNCTION_NESTED_NOT_ALLOWED,
                    statement.getLine(),
                    statement.getColumn());
        }

        @Override
        public void visitReturnStatement(ReturnStatement statement) {
            if(haveTheReturn) {
                reporter.report(
                        StatusCode.FUNCTION_HAS_EXTRA_RETURN,
                        statement.getLine(),
                        statement.getColumn(), functionName);
            }
            haveTheReturn = true;
        }

        @Override
        public void visitCompositeExpression(CompositeExpression expression) {
        }

        @Override
        public void visitFunctionCallExpression(FunctionCallExpression expression) {
        }

        @Override
        public void visitSymbolExpression(SymbolExpression expression) {
        }

        @Override
        public void visitValueExpression(ValueExpression expression) {
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
    }
}
