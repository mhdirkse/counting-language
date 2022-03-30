/*
 * Copyright Martijn Dirkse 2022
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

package com.github.mhdirkse.countlang.execution;

import com.github.mhdirkse.countlang.ast.AbstractLhs;
import com.github.mhdirkse.countlang.ast.ArrayExpression;
import com.github.mhdirkse.countlang.ast.ArrayTypeNode;
import com.github.mhdirkse.countlang.ast.AssignmentStatement;
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
import com.github.mhdirkse.countlang.ast.ForInRepetitionStatement;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FormalParameters;
import com.github.mhdirkse.countlang.ast.FunctionCallExpressionMember;
import com.github.mhdirkse.countlang.ast.FunctionCallExpressionNonMember;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.IfStatement;
import com.github.mhdirkse.countlang.ast.MarkUsedStatement;
import com.github.mhdirkse.countlang.ast.NonValueReturnStatement;
import com.github.mhdirkse.countlang.ast.Operator;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.ast.ProcedureCallStatement;
import com.github.mhdirkse.countlang.ast.ProcedureDefinitionStatement;
import com.github.mhdirkse.countlang.ast.RangeExpression;
import com.github.mhdirkse.countlang.ast.RepeatStatement;
import com.github.mhdirkse.countlang.ast.ValueReturnStatement;
import com.github.mhdirkse.countlang.ast.SampleMultipleStatement;
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
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.ast.Visitor;
import com.github.mhdirkse.countlang.ast.WhileStatement;
import com.github.mhdirkse.countlang.type.CountlangTuple;

class VariableAssigner implements Visitor {
    private final ExecutionContext context;
    private final ExpressionNode expressionNode;
    private final Object writtenValue;

    VariableAssigner(ExecutionContext context, ExpressionNode expressionNode, Object writtenValue) {
        this.context = context;
        this.expressionNode = expressionNode;
        this.writtenValue = writtenValue;
    }

    void assign(AbstractLhs lhs) {
        lhs.accept(this);
    }

    @Override
    public void visitStatementGroup(StatementGroup statementGroup) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitAssignmentStatement(AssignmentStatement statement) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitSampleStatement(SampleStatement statement) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitSampleMultipleStatement(SampleMultipleStatement statement) {
        throw new IllegalStateException("Method call not applicable");    	
    }

    @Override
    public void visitPrintStatement(PrintStatement statement) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitMarkUsedStatement(MarkUsedStatement statement) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitFunctionDefinitionStatement(FunctionDefinitionStatement statement) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitExperimentDefinitionStatement(ExperimentDefinitionStatement statement) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitValueReturnStatement(ValueReturnStatement statement) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitIfStatement(IfStatement ifStatement) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitWhileStatement(WhileStatement whileStatement) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitRepeatStatement(RepeatStatement repeatStatement) {
        throw new IllegalStateException("Method call not applicable");    	
    }

    @Override
    public void visitForInRepetitionStatement(ForInRepetitionStatement statement) {
        throw new IllegalStateException("Method call not applicable");    	    	
    }

    @Override
    public void visitCompositeExpression(CompositeExpression expression) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitRangeExpression(RangeExpression expression) {
        throw new IllegalStateException("Method call not applicable");    	
    }

    @Override
    public void visitFunctionCallExpressionNonMember(FunctionCallExpressionNonMember expression) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitFunctionCallExpressionMember(FunctionCallExpressionMember expression) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitSymbolExpression(SymbolExpression expression) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitValueExpression(ValueExpression expression) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitOperator(Operator operator) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitFormalParameters(FormalParameters formalParameters) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitFormalParameter(FormalParameter formalParameter) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitSimpleDistributionExpression(SimpleDistributionExpression expr) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitDistributionExpressionWithTotal(DistributionExpressionWithTotal expr) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitDistributionExpressionWithUnknown(DistributionExpressionWithUnknown expr) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitDistributionItemItem(DistributionItemItem item) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitDistributionItemCount(DistributionItemCount item) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitArrayExpression(ArrayExpression item) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitDereferenceExpression(DereferenceExpression item) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitTupleExpression(TupleExpression tupleExpression) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitAtomicTypeNode(AtomicTypeNode item) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitDistributionTypeNode(DistributionTypeNode item) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitArrayTypeNode(ArrayTypeNode item) {
        throw new IllegalStateException("Method call not applicable");
    }

	@Override
	public void visitProcedureDefinitionStatement(ProcedureDefinitionStatement statement) {
        throw new IllegalStateException("Method call not applicable");
	}

	@Override
	public void visitNonValueReturnStatement(NonValueReturnStatement statement) {
        throw new IllegalStateException("Method call not applicable");				
	}

	@Override
	public void visitProcedureCallStatement(ProcedureCallStatement statement) {
        throw new IllegalStateException("Method call not applicable");				
	}

    @Override
    public void visitTupleTypeNode(TupleTypeNode item) {
        throw new IllegalStateException("Method call not applicable");
    }

    @Override
    public void visitSimpleLhs(SimpleLhs lhs) {
        context.writeSymbol(lhs.getSymbol(), writtenValue, expressionNode);
    }

    @Override
    public void visitTupleDealingLhs(TupleDealingLhs lhs) {
        lhs.getChildren().forEach(c -> c.accept(this));
    }

    @Override
    public void visitTupleDealingLhsItemSkipped(TupleDealingLhsItemSkipped item) {
    }

    @Override
    public void visitTupleDealingLhsSymbol(TupleDealingLhsSymbol item) {
        int variableNumber = item.getVariableNumber();
        Object tupleMemberValue = ((CountlangTuple) writtenValue).get(variableNumber);
        context.writeSymbol(item.getSymbol(), tupleMemberValue, expressionNode);
    }
}
