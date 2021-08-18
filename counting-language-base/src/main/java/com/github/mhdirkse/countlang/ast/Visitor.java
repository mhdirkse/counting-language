/*
 * Copyright Martijn Dirkse 2020
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

package com.github.mhdirkse.countlang.ast;

public interface Visitor {
    public void visitStatementGroup(final StatementGroup statementGroup);
    public void visitAssignmentStatement(final AssignmentStatement statement);
    public void visitSampleStatement(final SampleStatement statement);
    public void visitPrintStatement(final PrintStatement statement);
    public void visitMarkUsedStatement(final MarkUsedStatement statement);
    public void visitFunctionDefinitionStatement(final FunctionDefinitionStatement statement);
    public void visitExperimentDefinitionStatement(final ExperimentDefinitionStatement statement);
    public void visitReturnStatement(final ReturnStatement statement);
    public void visitIfStatement(final IfStatement ifStatement);
    public void visitWhileStatement(final WhileStatement whileStatement);
    public void visitCompositeExpression(final CompositeExpression expression);
    public void visitFunctionCallExpressionNonMember(final FunctionCallExpressionNonMember expression);
    public void visitFunctionCallExpressionMember(final FunctionCallExpressionMember expression);
    public void visitSymbolExpression(final SymbolExpression expression);
    public void visitValueExpression(final ValueExpression expression);
    public void visitOperator(final Operator operator);
    public void visitFormalParameters(final FormalParameters formalParameters);
    public void visitFormalParameter(final FormalParameter formalParameter);
    public void visitSimpleDistributionExpression(final SimpleDistributionExpression expr);
    public void visitDistributionExpressionWithTotal(final DistributionExpressionWithTotal expr);
    public void visitDistributionExpressionWithUnknown(final DistributionExpressionWithUnknown expr);
    public void visitDistributionItemItem(DistributionItemItem item);
    public void visitDistributionItemCount(DistributionItemCount item);
    public void visitArrayExpression(ArrayExpression item);
    public void visitDereferenceExpression(DereferenceExpression item);
    public void visitTupleExpression(TupleExpression tupleExpression);
    public void visitAtomicTypeNode(AtomicTypeNode item);
    public void visitDistributionTypeNode(DistributionTypeNode item);
    public void visitArrayTypeNode(ArrayTypeNode item);
    public void visitTupleTypeNode(TupleTypeNode item);
    public void visitSimpleLhs(SimpleLhs lhs);
    public void visitTupleDealingLhs(TupleDealingLhs lhs);
    public void visitTupleDealingLhsItemSkipped(TupleDealingLhsItemSkipped item);
    public void visitTupleDealingLhsSymbol(TupleDealingLhsSymbol item);
}
