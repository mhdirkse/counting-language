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

package com.github.mhdirkse.countlang.execution;

import com.github.mhdirkse.countlang.ast.ArrayTypeNode;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.AtomicTypeNode;
import com.github.mhdirkse.countlang.ast.DistributionTypeNode;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FormalParameters;
import com.github.mhdirkse.countlang.ast.Operator;
import com.github.mhdirkse.countlang.ast.SimpleLhs;
import com.github.mhdirkse.countlang.ast.TupleDealingLhs;
import com.github.mhdirkse.countlang.ast.TupleDealingLhsItemSkipped;
import com.github.mhdirkse.countlang.ast.TupleDealingLhsSymbol;
import com.github.mhdirkse.countlang.ast.TupleTypeNode;
import com.github.mhdirkse.countlang.ast.Visitor;

abstract class AbstractAstNodeExecutionFactory implements AstNodeExecutionFactory, Visitor {
    AstNodeExecution result;
    Object context;

    @Override
    public AstNodeExecution create(AstNode node, Object context) {
        this.context = context;
        node.accept(this);
        AstNodeExecution currentResult = result;
        result = null;
        return currentResult;
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
    public void visitAtomicTypeNode(AtomicTypeNode typeNode) {
        // Nothing to do. Type nodes have been analyzed already and we have CountlangType here.
    }

    @Override
    public void visitDistributionTypeNode(DistributionTypeNode typeNode) {
        // Nothing to do. Type nodes have been analyzed already and we have CountlangType here.
    }

    @Override
    public void visitArrayTypeNode(ArrayTypeNode typeNode) {
        // Nothing to do. Type nodes have been analyzed already and we have CountlangType here.
    }

    @Override
    public void visitTupleTypeNode(TupleTypeNode typeNode) {
        // Nothing to do. Type nodes have been analyzed already and we have CountlangType here.
    }

    @Override
    public void visitSimpleLhs(SimpleLhs lhs) {
        // Nothing to do. The lhs does not produce values.
    }

    @Override
    public void visitTupleDealingLhs(TupleDealingLhs lhs) {
    }

    @Override
    public void visitTupleDealingLhsItemSkipped(TupleDealingLhsItemSkipped item) {
    }

    @Override
    public void visitTupleDealingLhsSymbol(TupleDealingLhsSymbol item) {
    }
}
