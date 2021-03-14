/*
 * Copyright Martijn Dirkse 2021
 *
 * This file is part of counting-language.
 *
 * counting-language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.mhdirkse.countlang.analysis;

import java.util.List;
import java.util.stream.Collectors;

import com.github.mhdirkse.countlang.algorithm.ScopeAccess;
import com.github.mhdirkse.countlang.ast.AbstractDistributionExpression;
import com.github.mhdirkse.countlang.ast.AbstractDistributionItem;
import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithTotal;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithUnknown;
import com.github.mhdirkse.countlang.ast.DistributionItemCount;
import com.github.mhdirkse.countlang.ast.DistributionItemItem;
import com.github.mhdirkse.countlang.ast.EmptyCollectionExpression;
import com.github.mhdirkse.countlang.ast.ExperimentDefinitionStatement;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FormalParameters;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatementBase;
import com.github.mhdirkse.countlang.ast.FunctionDefinitions;
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
import com.github.mhdirkse.countlang.tasks.SortingStatusReporter;
import com.github.mhdirkse.countlang.tasks.StatusCode;
import com.github.mhdirkse.countlang.tasks.StatusReporter;

public class Analysis {
    private final FunctionDefinitions funDefs;
    private final CodeBlocks codeBlocks;
    private FunctionDefinitionStatementBase analyzedFunction = null;
    int distributionItemIndex = -1;

    public Analysis(List<FunctionDefinitionStatement> funDefs) {
        this.funDefs = new FunctionDefinitions();
        funDefs.forEach(fds -> this.funDefs.putFunction(fds));
        codeBlocks = new CodeBlocks(new MemoryImpl());
    }

    public void analyze(StatementGroup rootStatement, StatusReporter reporterDelegate) {
        SortingStatusReporter reporter = new SortingStatusReporter();
        AnalysisVisitor v = new AnalysisVisitor(reporter);
        codeBlocks.start();
        rootStatement.accept(v);
        codeBlocks.stop();
        codeBlocks.report(reporter);
        reporter.reportTo(reporterDelegate);
    }

    private class AnalysisVisitor implements Visitor {
        private final StatusReporter reporter;

        AnalysisVisitor(StatusReporter reporter) {
            this.reporter = reporter;
        }

        @Override
        public void visitStatementGroup(StatementGroup statementGroup) {
            codeBlocks.pushScope(new AnalysisScope(ScopeAccess.SHOW_PARENT));
            for(AstNode statement: statementGroup.getChildren()) {
                codeBlocks.handleStatement(statement.getLine(), statement.getColumn());
                statement.accept(this);
            }
            codeBlocks.popScope();
        }

        @Override
        public void visitAssignmentStatement(AssignmentStatement statement) {
            ExpressionNode rhs = statement.getRhs();
            rhs.accept(this);
            codeBlocks.write(statement.getLhs(), statement.getLine(), statement.getColumn(), rhs.getCountlangType());
        }

        @Override
        public void visitSampleStatement(SampleStatement statement) {
            if(analyzedFunction == null) {
                reporter.report(StatusCode.SAMPLING_OUTSIDE_EXPERIMENT, statement.getLine(), statement.getColumn());
            }
            statement.getSampledDistribution().accept(this);
            CountlangType actualType = statement.getSampledDistribution().getCountlangType();
            if(!actualType.isDistribution()) {
                reporter.report(StatusCode.SAMPLED_FROM_NON_DISTRIBUTION, statement.getLine(), statement.getColumn(), actualType.toString());
            }
            codeBlocks.write(statement.getSymbol(), statement.getLine(), statement.getColumn(), actualType.getSubType());
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
            analyzeFunctionDefinitionBase(statement, () -> codeBlocks.startFunction(statement.getLine(), statement.getColumn(), statement.getName()));
        }
        
        private void analyzeFunctionDefinitionBase(FunctionDefinitionStatementBase statement, Runnable blockCreator) {
            if(! codeBlocks.isAtRootLevel()) {
                reporter.report(StatusCode.FUNCTION_NESTED_NOT_ALLOWED, statement.getLine(), statement.getColumn());
            }
            else if(funDefs.hasFunction(statement.getName())) {
                reporter.report(StatusCode.FUNCTION_ALREADY_DEFINED, statement.getLine(), statement.getColumn(), statement.getName());
            } else {
                analyzeFunction(statement, blockCreator);
                funDefs.putFunction(statement);
            }
        }

        private void analyzeFunction(FunctionDefinitionStatementBase statement, Runnable blockCreator) {
            analyzedFunction = statement;
            blockCreator.run();
            codeBlocks.pushScope(new AnalysisScope(ScopeAccess.HIDE_PARENT));
            for(FormalParameter p: statement.getFormalParameters().getFormalParameters()) {
                codeBlocks.addParameter(p.getName(), p.getLine(), p.getColumn(), p.getCountlangType());
            }
            statement.getStatements().accept(this);
            codeBlocks.popScope();
            codeBlocks.stopFunction();
            analyzedFunction = null;
        }

        @Override
        public void visitExperimentDefinitionStatement(ExperimentDefinitionStatement statement) {
            analyzeFunctionDefinitionBase(statement, () -> codeBlocks.startExperiment(statement.getLine(), statement.getColumn(), statement.getName()));
        }

        @Override
        public void visitReturnStatement(ReturnStatement statement) {
            statement.getExpression().accept(this);
            CountlangType newReturnType = statement.getExpression().getCountlangType();
            codeBlocks.handleReturn(statement.getLine(), statement.getColumn());
            if(newReturnType == CountlangType.unknown()) {
                // UNKNOWN should appear here because of earlier errors, no need to report something.
                return;
            }
            if(analyzedFunction instanceof FunctionDefinitionStatement) {
                FunctionDefinitionStatement f = (FunctionDefinitionStatement) analyzedFunction;
                if(f.getReturnType() == CountlangType.unknown()) {
                    f.setReturnType(newReturnType);
                } else if(newReturnType != f.getReturnType()) {
                    reporter.report(StatusCode.FUNCTION_RETURN_TYPE_MISMATCH, f.getLine(), f.getColumn(), newReturnType.toString(), f.getReturnType().toString());
                }
            } else if(analyzedFunction instanceof ExperimentDefinitionStatement) {
                ExperimentDefinitionStatement f = (ExperimentDefinitionStatement) analyzedFunction;
                if(f.getReturnType() == CountlangType.unknown()) {
                    f.setReturnType(CountlangType.distributionOf(newReturnType));
                } else if(newReturnType != f.getReturnType().getSubType()) {
                    reporter.report(StatusCode.DISTRIBUTION_RETURN_TYPE_MISMATCH, f.getLine(), f.getColumn(), newReturnType.toString(), f.getReturnType().toString());
                }
            }
        }

        @Override
        public void visitIfStatement(IfStatement ifStatement) {
            ifStatement.getSelector().accept(this);
            if(ifStatement.getSelector().getCountlangType() != CountlangType.bool()) {
                reporter.report(StatusCode.IF_SELECT_NOT_BOOLEAN, ifStatement.getLine(), ifStatement.getColumn(), ifStatement.getSelector().getCountlangType().toString());
            }
            codeBlocks.startSwitch();
            codeBlocks.startBranch();
            ifStatement.getThenStatement().accept(this);
            codeBlocks.stopBranch();
            codeBlocks.startBranch();
            if(ifStatement.getElseStatement() != null) {
                ifStatement.getElseStatement().accept(this);
            }
            codeBlocks.stopBranch();
            codeBlocks.stopSwitch();
        }

        @Override
        public void visitWhileStatement(WhileStatement whileStatement) {
            codeBlocks.startRepetition();
            whileStatement.getTestExpr().accept(this);
            if(whileStatement.getTestExpr().getCountlangType() != CountlangType.bool()) {
                reporter.report(StatusCode.WHILE_TEST_NOT_BOOLEAN, whileStatement.getLine(), whileStatement.getColumn(), whileStatement.getTestExpr().getCountlangType().toString());
            }
            whileStatement.getStatement().accept(this);
            codeBlocks.stopRepetition();
        }

        @Override
        public void visitCompositeExpression(CompositeExpression expression) {
            expression.getSubExpressions().forEach(ex -> ex.accept(this));
            expression.setCountlangType(CountlangType.unknown());
            List<CountlangType> types = expression.getSubExpressions().stream().map(ExpressionNode::getCountlangType).collect(Collectors.toList());
            if(types.size() != expression.getOperator().getNumArguments()) {
                reporter.report(StatusCode.OPERATOR_ARGUMENT_COUNT_MISMATCH, expression.getLine(), expression.getColumn(),
                        expression.getOperator().getName(), new Integer(expression.getOperator().getNumArguments()).toString(), new Integer(types.size()).toString());
            }
            boolean typesOk = expression.getOperator().checkAndEstablishTypes(types);
            if(! typesOk) {
                reporter.report(StatusCode.OPERATOR_TYPE_MISMATCH, expression.getLine(), expression.getColumn(), expression.getOperator().getName());
            }
            expression.setCountlangType(expression.getOperator().getResultType());
        }

        @Override
        public void visitFunctionCallExpression(FunctionCallExpression expression) {
            expression.setCountlangType(CountlangType.unknown());
            expression.getSubExpressions().forEach(ex -> ex.accept(this));
            if(! funDefs.hasFunction(expression.getFunctionName())) {
                reporter.report(StatusCode.FUNCTION_DOES_NOT_EXIST, expression.getLine(), expression.getColumn(), expression.getFunctionName());
                return;
            }
            FunctionDefinitionStatementBase fun = funDefs.getFunction(expression.getFunctionName());
            expression.setCountlangType(fun.getReturnType());
            if(expression.getNumArguments() != fun.getNumParameters()) {
                reporter.report(StatusCode.FUNCTION_ARGUMENT_COUNT_MISMATCH, expression.getLine(), expression.getColumn(), expression.getFunctionName(),
                        new Integer(fun.getNumParameters()).toString(), new Integer(expression.getNumArguments()).toString());
                return;
            } else {
                List<CountlangType> types = expression.getSubExpressions().stream().map(ExpressionNode::getCountlangType).collect(Collectors.toList());
                for(int i = 0; i < expression.getNumArguments(); i++) {
                    if(types.get(i) != fun.getFormalParameterType(i)) {
                        reporter.report(StatusCode.FUNCTION_TYPE_MISMATCH, expression.getLine(), expression.getColumn(), expression.getFunctionName(), new Integer(i).toString());
                        return;
                    }
                }
            }
        }

        @Override
        public void visitSymbolExpression(SymbolExpression expression) {
            expression.setCountlangType(codeBlocks.read(expression.getSymbol(), expression.getLine(), expression.getColumn()));
        }

        @Override
        public void visitValueExpression(ValueExpression expression) {
        }

        @Override
        public void visitEmptyCollectionExpression(EmptyCollectionExpression expression) {
            if(expression.getCountlangType().isPrimitive()) {
                reporter.report(StatusCode.EMPTY_COLLECTION_IS_PRIMITIVE, expression.getLine(), expression.getColumn());
            }
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
            genericHandleDistributionExpression(expr);
        }

        private void genericHandleDistributionExpression(AbstractDistributionExpression expr) {
            for(distributionItemIndex = 0; distributionItemIndex < expr.getNumScoredValues(); distributionItemIndex++) {
                AbstractDistributionItem subExpression = expr.getScoredValues().get(distributionItemIndex);
                subExpression.accept(this);
                CountlangType subExpressionType = subExpression.getItem().getCountlangType();
                if(expr.getCountlangType() == CountlangType.unknown()) {
                    expr.setCountlangType(CountlangType.distributionOf(subExpressionType));
                } else if(subExpressionType != expr.getCountlangType().getSubType()) {
                    reporter.report(StatusCode.DISTRIBUTION_SCORED_VALUE_TYPE_MISMATCH, subExpression.getLine(), subExpression.getColumn(),
                            new Integer(distributionItemIndex + 1).toString(), subExpressionType.toString(), expr.getCountlangType().getSubType().toString());
                }
            }
        }

        @Override
        public void visitDistributionExpressionWithTotal(DistributionExpressionWithTotal expr) {
            genericHandleDistributionExpression(expr);
            ExpressionNode extra = (ExpressionNode) expr.getChildren().get(expr.getNumSubExpressions() - 1);
            checkExtraValue(extra);
        }

        void checkExtraValue(ExpressionNode extra) {
            if(extra.getCountlangType() != CountlangType.integer()) {
                reporter.report(StatusCode.DISTRIBUTION_AMOUNT_NOT_INT, extra.getLine(), extra.getColumn());
            }
        }

        @Override
        public void visitDistributionExpressionWithUnknown(DistributionExpressionWithUnknown expr) {
            genericHandleDistributionExpression(expr);
            ExpressionNode extra = (ExpressionNode) expr.getChildren().get(expr.getNumSubExpressions() - 1);
            checkExtraValue(extra);
        }

        @Override
        public void visitDistributionItemItem(DistributionItemItem item) {
            item.getChildren().forEach(c -> c.accept(this));
        }

        @Override
        public void visitDistributionItemCount(DistributionItemCount item) {
            item.getChildren().forEach(c -> c.accept(this));
            CountlangType actual = item.getCount().getCountlangType();
            if(actual != CountlangType.integer()) {
                reporter.report(StatusCode.DISTRIBUTION_SCORED_COUNT_NOT_INT, item.getLine(), item.getColumn(), new Integer(distributionItemIndex + 1).toString(), actual.toString());
            }
        }        
    }
}
