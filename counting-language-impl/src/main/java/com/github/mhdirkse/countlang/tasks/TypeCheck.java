package com.github.mhdirkse.countlang.tasks;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.ast.AbstractAstListener;
import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.AstVisitorToListener;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FormalParameters;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.Operator;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.ast.ReturnStatement;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.TestFunctionDefinitions;
import com.github.mhdirkse.countlang.execution.CountlangType;
import com.github.mhdirkse.countlang.execution.ExecutionContext;
import com.github.mhdirkse.countlang.execution.ExecutionContextImpl;
import com.github.mhdirkse.countlang.execution.OutputStrategy;
import com.github.mhdirkse.countlang.execution.RunnableFunction;

class TypeCheck {
    private final StatusReporter reporter;
    private final StatementGroup statementGroup;
    
    TypeCheck(final StatusReporter reporter, final StatementGroup statementGroup) {
        this.reporter = reporter;
        this.statementGroup = statementGroup;
    }

    void run() {
        FunctionDefinitionStatement testFunction = TestFunctionDefinitions.createTestFunction();
        ExecutionContextImpl executionContext = new ExecutionContextImpl(OutputStrategy.NO_OUTPUT);
        executionContext.putFunction(testFunction);
        Listener l = new Listener(executionContext, reporter);
        new AstVisitorToListener(l).visitStatementGroup(statementGroup);
    }

    private static class Listener extends AbstractAstListener {
        private final StatusReporter reporter;
        private final ExecutionContext ctx;
        private FunctionDefinitionStatement returnOwner = null;

        Listener(final ExecutionContext ctx, StatusReporter reporter) {
            this.reporter = reporter;
            this.ctx = ctx;
        }

        @Override
        public void enterFunctionDefinitionStatement(final FunctionDefinitionStatement stmt) {
            if(returnOwner != null) {
                // This should have been checked already
                throw new IllegalStateException("Unexpected function definition statement");
            }
            returnOwner = stmt;
        }

        @Override
        public void enterFormalParameters(final FormalParameters formalParameters) {
            ctx.startPreparingNewFrame();            
        }

        @Override
        public void visitFormalParameter(final FormalParameter formalParameter) {
            ctx.putSymbolInNewFrame(formalParameter.getName(), formalParameter.getCountlangType().getExample());            
        }

        @Override
        public void exitFormalParameters(final FormalParameters formalParameters) {
            ctx.pushNewFrame();
        }

        @Override
        public void exitFunctionDefinitionStatement(final FunctionDefinitionStatement stmt) {
            ctx.putFunction(stmt);
            ctx.popFrame();
            returnOwner = null;
        }

        @Override
        public void exitAssignmentStatement(final AssignmentStatement stmt) {
            ctx.putSymbol(stmt.getLhs(), stmt.getRhs().getCountlangType().getExample());
        }

        @Override
        public void visitSymbolExpression(final SymbolExpression expr) {
            String sym = expr.getSymbol();
            if(!ctx.hasSymbol(sym)) {
                reporter.report(StatusCode.VAR_UNDEFINED, expr.getLine(), expr.getColumn(), sym);
            }
            expr.setCountlangType(ctx.getType(sym));
        }

        @Override
        public void exitCompositeExpression(final CompositeExpression expr) {
            List<CountlangType> argumentTypes = new ArrayList<>();
            for(int i = 0; i < expr.getNumSubExpressions(); i++) {
                argumentTypes.add(expr.getSubExpression(i).getCountlangType());
            }
            Operator op = expr.getOperator();
            if(argumentTypes.size() != op.getNumArguments()) {
                reporter.report(
                        StatusCode.OPERATOR_ARGUMENT_COUNT_MISMATCH,
                        op.getLine(),
                        op.getColumn(),
                        op.getName(),
                        Integer.toString(op.getNumArguments()),
                        Integer.toString(argumentTypes.size()));
                return;
            }
            if(!op.checkAndEstablishTypes(argumentTypes)) {
                reporter.report(
                        StatusCode.OPERATOR_TYPE_MISMATCH,
                        op.getLine(),
                        op.getColumn(),
                        op.getName());
                return;
            }
            expr.setCountlangType(op.getResultType());
        }

        @Override
        public void exitFunctionCallExpression(final FunctionCallExpression expr) {
            List<CountlangType> actualParameterTypes = new ArrayList<CountlangType>();
            for(int i = 0; i < expr.getNumArguments(); i++) {
                actualParameterTypes.add(expr.getArgument(i).getCountlangType());
            }
            if(!ctx.hasFunction(expr.getFunctionName())) {
                reporter.report(
                        StatusCode.FUNCTION_DOES_NOT_EXIST,
                        expr.getLine(),
                        expr.getColumn(),
                        expr.getFunctionName());
                return;
            }
            RunnableFunction fun = ctx.getFunction(expr.getFunctionName());
            for(int i = 0; i < expr.getNumArguments(); i++) {
                if(actualParameterTypes.get(i) != fun.getFormalParameterType(i)) {
                    reporter.report(
                            StatusCode.FUNCTION_TYPE_MISMATCH,
                            expr.getLine(),
                            expr.getColumn(),
                            expr.getFunctionName(),
                            fun.getFormalParameterName(i));
                }
            }
            expr.setCountlangType(fun.getReturnType());
        }

        @Override
        public void exitReturnStatement(final ReturnStatement stmt) {
            if(returnOwner == null) {
                // This should have been checked already
                throw new IllegalStateException("Unexpected return statement");
            }
            returnOwner.setReturnType(stmt.getExpression().getCountlangType());
            // Protect against multiple returns in same function.
            returnOwner = null;
        }
    }
}
