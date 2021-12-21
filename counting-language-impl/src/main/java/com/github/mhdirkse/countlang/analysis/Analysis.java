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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.mhdirkse.countlang.algorithm.ScopeAccess;
import com.github.mhdirkse.countlang.ast.AbstractDistributionExpression;
import com.github.mhdirkse.countlang.ast.AbstractDistributionItem;
import com.github.mhdirkse.countlang.ast.AbstractLhs;
import com.github.mhdirkse.countlang.ast.ArrayExpression;
import com.github.mhdirkse.countlang.ast.ArrayTypeNode;
import com.github.mhdirkse.countlang.ast.AssignmentStatement;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.AtomicTypeNode;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.DereferenceExpression;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithTotal;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithUnknown;
import com.github.mhdirkse.countlang.ast.DistributionItemCount;
import com.github.mhdirkse.countlang.ast.DistributionItemItem;
import com.github.mhdirkse.countlang.ast.DistributionTypeNode;
import com.github.mhdirkse.countlang.ast.ExperimentDefinitionStatement;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FormalParameters;
import com.github.mhdirkse.countlang.ast.FunctionCallErrorHandler;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionCallExpressionMember;
import com.github.mhdirkse.countlang.ast.FunctionCallExpressionNonMember;
import com.github.mhdirkse.countlang.ast.FunctionDefinition;
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
import com.github.mhdirkse.countlang.ast.SimpleLhs;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.TupleDealingLhs;
import com.github.mhdirkse.countlang.ast.TupleDealingLhsItemSkipped;
import com.github.mhdirkse.countlang.ast.TupleDealingLhsSymbol;
import com.github.mhdirkse.countlang.ast.TupleExpression;
import com.github.mhdirkse.countlang.ast.TupleTypeNode;
import com.github.mhdirkse.countlang.ast.TypeNode;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.ast.Visitor;
import com.github.mhdirkse.countlang.ast.WhileStatement;
import com.github.mhdirkse.countlang.tasks.SortingStatusReporter;
import com.github.mhdirkse.countlang.tasks.StatusCode;
import com.github.mhdirkse.countlang.tasks.StatusReporter;
import com.github.mhdirkse.countlang.type.CountlangType;
import com.github.mhdirkse.countlang.type.TupleType;

public class Analysis {
    private final FunctionDefinitions funDefs;
    private final CodeBlocks codeBlocks;
    private FunctionDefinitionStatementBase analyzedFunction = null;
    int distributionItemIndex = -1;
    
    // Assignment statements cannot get nested. The RHS consists of expressions, which do not contain statements.
    // If this ever changes, please change these two into a stack.
    AstNode assignmentStatementOfLhs = null;
    CountlangType rhsType = null;

    public Analysis(List<FunctionDefinition> funDefs) {
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
            assignmentStatementOfLhs = statement;
            rhsType = rhs.getCountlangType();
            AbstractLhs lhs = statement.getLhs();
            lhs.accept(this);
            assignmentStatementOfLhs = null;
            rhsType = null;
        }

        @Override
        public void visitSampleStatement(SampleStatement statement) {
            if(analyzedFunction == null) {
                reporter.report(StatusCode.SAMPLING_OUTSIDE_EXPERIMENT, statement.getLine(), statement.getColumn());
                return;
            }
            statement.getSampledDistribution().accept(this);
            CountlangType actualType = statement.getSampledDistribution().getCountlangType();
            if(!actualType.isDistribution()) {
                reporter.report(StatusCode.SAMPLED_FROM_NON_DISTRIBUTION, statement.getLine(), statement.getColumn(), actualType.toString());
                return;
            }
            assignmentStatementOfLhs = statement;
            rhsType = actualType.getSubType();
            statement.getLhs().accept(this);
            assignmentStatementOfLhs = null;
            rhsType = null;
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
            analyzeFunctionDefinitionBase(statement, () -> codeBlocks.startFunction(statement.getLine(), statement.getColumn(), statement.getKey()));
        }
        
        private void analyzeFunctionDefinitionBase(FunctionDefinitionStatementBase statement, Runnable blockCreator) {
            if(! codeBlocks.isAtRootLevel()) {
                reporter.report(StatusCode.FUNCTION_NESTED_NOT_ALLOWED, statement.getLine(), statement.getColumn());
                return;
            }
            else if(funDefs.hasFunction(statement.getKey())) {
                reporter.report(StatusCode.FUNCTION_ALREADY_DEFINED, statement.getLine(), statement.getColumn(), statement.getKey().toString());
                return;
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
                p.accept(this);
                codeBlocks.addParameter(p.getName(), p.getLine(), p.getColumn(), p.getCountlangType());
            }
            statement.getStatements().accept(this);
            codeBlocks.popScope();
            codeBlocks.stopFunction();
            analyzedFunction = null;
        }

        @Override
        public void visitExperimentDefinitionStatement(ExperimentDefinitionStatement statement) {
            analyzeFunctionDefinitionBase(statement, () -> codeBlocks.startExperiment(statement.getLine(), statement.getColumn(), statement.getKey()));
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
                return;
            }
            boolean typesOk = expression.getOperator().checkAndEstablishTypes(types);
            if(! typesOk) {
                reporter.report(StatusCode.OPERATOR_TYPE_MISMATCH, expression.getLine(), expression.getColumn(), expression.getOperator().getName());
                return;
            }
            expression.setCountlangType(expression.getOperator().getResultType());
        }

        @Override
        public void visitFunctionCallExpressionNonMember(FunctionCallExpressionNonMember expression) {
            analyzeFunctionCallExpression(expression, new FunctionCallErrorHandlerNonMember(expression));
        }

        @Override
        public void visitFunctionCallExpressionMember(FunctionCallExpressionMember expression) {
            analyzeFunctionCallExpression(expression, new FunctionCallErrorHandlerMember(expression));
        }

        private void analyzeFunctionCallExpression(FunctionCallExpression expression, FunctionCallErrorHandler errorHandler) {
            expression.setCountlangType(CountlangType.unknown());
            expression.getSubExpressions().forEach(ex -> ex.accept(this));
            if(! funDefs.hasFunction(expression.getKey())) {
                reporter.report(StatusCode.FUNCTION_DOES_NOT_EXIST, expression.getLine(), expression.getColumn(), expression.getKey().toString());
                return;
            }
            FunctionDefinition fun = funDefs.getFunction(expression.getKey());
            List<CountlangType> arguments = expression.getSubExpressions().stream().map(ExpressionNode::getCountlangType).collect(Collectors.toList());
            CountlangType returnType = fun.checkCallAndGetReturnType(arguments, errorHandler);
            if(returnType != CountlangType.unknown()) {
                expression.setCountlangType(returnType);
            }            
        }

        private class FunctionCallErrorHandlerNonMember implements FunctionCallErrorHandler {
            private FunctionCallExpression expression;

            FunctionCallErrorHandlerNonMember(FunctionCallExpression expression) {
                this.expression = expression;
            }

            @Override
            public void handleParameterCountMismatch(int numExpected, int numActual) {
                reporter.report(StatusCode.FUNCTION_ARGUMENT_COUNT_MISMATCH, expression.getLine(), expression.getColumn(), expression.getKey().toString(),
                        new Integer(numExpected).toString(), new Integer(numActual).toString());                    
            }

            @Override
            public void handleParameterTypeMismatch(int parameterNumber, CountlangType expectedType, CountlangType actualType) {
                reporter.report(StatusCode.FUNCTION_TYPE_MISMATCH, expression.getLine(), expression.getColumn(), expression.getKey().toString(), new Integer(parameterNumber + 1).toString());
            }                          
        }

        private class FunctionCallErrorHandlerMember implements FunctionCallErrorHandler {
            private FunctionCallExpression expression;

            FunctionCallErrorHandlerMember(FunctionCallExpression expression) {
                this.expression = expression;
            }

            @Override
            public void handleParameterCountMismatch(int numExpected, int numActual) {
                // In the error message, do not count the this argument.
                //
                // The this argument will never have a type mismatch, because the this arguments's type
                // is used to find the function.
                reporter.report(StatusCode.FUNCTION_ARGUMENT_COUNT_MISMATCH, expression.getLine(), expression.getColumn(), expression.getKey().toString(),
                        new Integer(numExpected - 1).toString(), new Integer(numActual - 1).toString());                    
            }

            @Override
            public void handleParameterTypeMismatch(int parameterNumber, CountlangType expectedType, CountlangType actualType) {
                // Do not count the this argument to index the parameter.
                reporter.report(StatusCode.FUNCTION_TYPE_MISMATCH, expression.getLine(), expression.getColumn(), expression.getKey().toString(), new Integer(parameterNumber).toString());
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
        public void visitOperator(Operator operator) {
        }

        @Override
        public void visitFormalParameters(FormalParameters formalParameters) {
        }

        @Override
        public void visitFormalParameter(FormalParameter formalParameter) {
            formalParameter.getTypeNode().accept(this);
        }

        @Override
        public void visitAtomicTypeNode(AtomicTypeNode typeNode) {
            // Nothing to do. There are no children and the countlang type is always known and correct.
        }

        @Override
        public void visitDistributionTypeNode(DistributionTypeNode typeNode) {
            typeNode.getChildren().forEach(c -> c.accept(this));
        }

        @Override
        public void visitArrayTypeNode(ArrayTypeNode typeNode) {
            typeNode.getChildren().forEach(c -> c.accept(this));            
        }

        @Override
        public void visitTupleTypeNode(TupleTypeNode typeNode) {
            typeNode.getChildren().forEach(c -> c.accept(this));
            if(typeNode.getChildren().size() <= 1) {
                reporter.report(StatusCode.TUPLE_AT_LEAST_TWO_MEMBERS, typeNode.getLine(), typeNode.getColumn());
            }
            if(typeNode.getChildren().stream().anyMatch(c -> ((TypeNode) c).getCountlangType().isTuple())) {
                reporter.report(StatusCode.TUPLES_MUST_BE_FLAT, typeNode.getLine(), typeNode.getColumn());
            }
        }

        @Override
        public void visitSimpleDistributionExpression(SimpleDistributionExpression expr) {
            genericHandleDistributionExpression(expr);
        }

        private void genericHandleDistributionExpression(AbstractDistributionExpression expr) {
            if(expr.getTypeNode() != null) {
                expr.getTypeNode().accept(this);
                expr.setCountlangType(expr.getTypeNode().getCountlangType());
            }
            for(distributionItemIndex = 0; distributionItemIndex < expr.getNumScoredValues(); distributionItemIndex++) {
                AbstractDistributionItem subExpression = expr.getScoredValues().get(distributionItemIndex);
                // By visiting the child here we ensure that the member variable
                // distributionItemIndex has been set.
                subExpression.accept(this);
                CountlangType subExpressionType = subExpression.getItem().getCountlangType();
                if(expr.getCountlangType() == CountlangType.unknown()) {
                    expr.setCountlangType(CountlangType.distributionOf(subExpressionType));
                } else if(subExpressionType != expr.getCountlangType().getSubType()) {
                    reporter.report(StatusCode.DISTRIBUTION_SCORED_VALUE_TYPE_MISMATCH, subExpression.getLine(), subExpression.getColumn(),
                            new Integer(distributionItemIndex + 1).toString(), subExpressionType.toString(), expr.getCountlangType().getSubType().toString());
                }
            }
            if((expr.getNumScoredValues() == 0) && (expr.getCountlangType() == CountlangType.unknown())) {
                reporter.report(StatusCode.UNTYPED_DISTRIBUTION, expr.getLine(), expr.getColumn());
            }
        }

        @Override
        public void visitDistributionExpressionWithTotal(DistributionExpressionWithTotal expr) {
            genericHandleDistributionExpression(expr);
            ExpressionNode extra = (ExpressionNode) expr.getNonTypeChildren().get(expr.getNumSubExpressions() - 1);
            checkExtraValue(extra);
        }

        void checkExtraValue(ExpressionNode extra) {
            extra.accept(this);
            if(extra.getCountlangType() != CountlangType.integer()) {
                reporter.report(StatusCode.DISTRIBUTION_AMOUNT_NOT_INT, extra.getLine(), extra.getColumn());
            }
        }

        @Override
        public void visitDistributionExpressionWithUnknown(DistributionExpressionWithUnknown expr) {
            genericHandleDistributionExpression(expr);
            ExpressionNode extra = (ExpressionNode) expr.getNonTypeChildren().get(expr.getNumSubExpressions() - 1);
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

        @Override
        public void visitArrayExpression(ArrayExpression expr) {
            expr.getChildren().forEach(c -> c.accept(this));
            if(expr.getTypeNode() != null) {
                expr.setCountlangType(expr.getTypeNode().getCountlangType());
            }
            List<ExpressionNode> members = expr.getElements();
            if(members.isEmpty()) {
                CountlangType countlangType = expr.getCountlangType();
                if(countlangType == CountlangType.unknown()) {
                    // The grammar should make this impossible
                    throw new IllegalStateException("The grammar should only allow an empty array with explicit type");
                } else if(! countlangType.isArray()) {
                    // The grammar should make this impossible
                    throw new IllegalStateException("The grammar should force array expressions to be of array type");
                }
            } else {
                // The grammar should prohibit an explicit type with a non-empty array
                CountlangType childType = members.get(0).getCountlangType();
                for(int i = 1; i < members.size(); ++i) {
                    ExpressionNode currentChild = members.get(i);
                    if(currentChild.getCountlangType() != childType) {
                        reporter.report(StatusCode.ARRAY_ELEMENT_TYPE_MISMATCH, expr.getLine(), expr.getColumn(),
                                Integer.valueOf(i+1).toString(), currentChild.getCountlangType().toString(), childType.toString());
                    }
                }
                expr.setCountlangType(CountlangType.arrayOf(childType));
            }
        }

        @Override
        public void visitTupleExpression(TupleExpression expr) {
            List<AstNode> children = expr.getChildren();
            children.forEach(c -> c.accept(this));
            if(children.size() <= 1) {
                reporter.report(StatusCode.TUPLE_AT_LEAST_TWO_MEMBERS, expr.getLine(), expr.getColumn());
            } else {
                List<CountlangType> childTypes = children.stream()
                        .map(c -> ((ExpressionNode) c).getCountlangType())
                        .collect(Collectors.toList());
                expr.setCountlangType(CountlangType.tupleOf(childTypes));
            }
        }
        
        @Override
        public void visitDereferenceExpression(DereferenceExpression expr) {
            expr.getChildren().forEach(c -> c.accept(this));
            CountlangType containerType = expr.getContainer().getCountlangType();
            if(! (containerType.isArray() || containerType.isTuple())) {
                reporter.report(StatusCode.MEMBER_OF_NON_ARRAY_OR_TUPLE, expr.getLine(), expr.getColumn());
            }
            List<ExpressionNode> references = expr.getReferences();
            for(int i = 0; i < references.size(); ++i) {
            	ExpressionNode reference = references.get(i);
                if(reference.getCountlangType() != CountlangType.integer()) {
                    reporter.report(StatusCode.MEMBER_INDEX_NOT_INT, expr.getLine(), expr.getColumn(), Integer.toString(i+1));
                }
            }
            if(containerType.isArray()) {
            	if(references.size() == 1) {
            		expr.setCountlangType(containerType.getSubType());
            	} else {
            		expr.setCountlangType(containerType);
            		expr.setArraySelector();
            	}
            } else if(containerType.isTuple()) {
            	if(references.size() == 1) {
            		expr.setCountlangType(getCountlangTypeForTupleIndex(references.get(0), (TupleType) containerType));
            	} else {
            		List<CountlangType> newSubTypes = new ArrayList<>();
            		for(ExpressionNode reference: references) {
            			CountlangType newSubType = getCountlangTypeForTupleIndex(reference, (TupleType) containerType);
            			if(newSubType == CountlangType.unknown()) {
            				expr.setCountlangType(CountlangType.unknown());
            				break;
            			}
            			newSubTypes.add(newSubType);
            		}
            		expr.setArraySelector();
            		expr.setCountlangType(CountlangType.tupleOf(newSubTypes));
            	}
            }
        }

        private CountlangType getCountlangTypeForTupleIndex(ExpressionNode reference, TupleType asTupleType) {
        	int tupleTypeSize = asTupleType.getNumSubTypes();
        	BigInteger bigTupleTypeSize = BigInteger.valueOf(tupleTypeSize);
        	if(! (reference instanceof ValueExpression)) {
        		reporter.report(StatusCode.TUPLE_INEX_MUST_BE_CONSTANT, reference.getLine(), reference.getColumn());
        		return CountlangType.unknown();
        	}
        	Object rawValue = ((ValueExpression) reference).getValue();
        	BigInteger bigTupleIndex = ((BigInteger) rawValue).subtract(BigInteger.ONE);
        	if(bigTupleIndex.compareTo(bigTupleTypeSize) >= 0) {
        		reporter.report(StatusCode.TUPLE_INDEX_OUT_OF_BOUNDS, reference.getLine(), reference.getColumn(), bigTupleIndex.add(BigInteger.ONE).toString());
        		return CountlangType.unknown();
        	}
        	int tupleIndex = bigTupleIndex.intValue();
        	return asTupleType.getTupleSubTypes().get(tupleIndex);
        }

        @Override
        public void visitSimpleLhs(SimpleLhs lhs) {
            codeBlocks.write(lhs.getSymbol(), assignmentStatementOfLhs.getLine(), assignmentStatementOfLhs.getColumn(), rhsType);            
        }

        @Override
        public void visitTupleDealingLhs(TupleDealingLhs lhs) {
            if(rhsType.isTuple()) {
            	int numValues = ((TupleType) rhsType).getNumSubTypes();
            	int numVars = lhs.getNumChildren();
            	if(numValues == numVars) {
            		lhs.getChildren().forEach(c -> c.accept(this));
            	} else {
            		reporter.report(StatusCode.TUPLE_DEALING_COUNT_MISMATCH, lhs.getLine(), lhs.getColumn(), Integer.toString(numValues), Integer.toString(numVars));
            	}
            } else {
                reporter.report(StatusCode.CANNOT_DEAL_VALUES_FROM_NON_TUPLE, assignmentStatementOfLhs.getLine(), assignmentStatementOfLhs.getColumn());
            }
        }

        @Override
        public void visitTupleDealingLhsItemSkipped(TupleDealingLhsItemSkipped item) {
        }

        @Override
        public void visitTupleDealingLhsSymbol(TupleDealingLhsSymbol item) {
            CountlangType countlangType = ((TupleType) rhsType).getTupleSubTypes().get(item.getVariableNumber());
            codeBlocks.write(item.getSymbol(), assignmentStatementOfLhs.getLine(), assignmentStatementOfLhs.getColumn(), countlangType);
        }
    }
}
