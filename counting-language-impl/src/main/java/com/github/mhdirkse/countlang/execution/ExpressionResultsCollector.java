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

import java.util.List;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CompositeNode;

abstract class ExpressionResultsCollector implements AstNodeExecution {
    final CompositeNode node;
    SubExpressionStepper delegate;

    ExpressionResultsCollector(final CompositeNode node) {
        this.node = node;
        delegate = new SubExpressionStepper(node.getSubExpressions());
    }

    @Override
    public final AstNode getAstNode() {
        return (AstNode) node;
    }

    @Override
    public final AstNodeExecutionState getState() {
        return delegate.getState();
    }

    @Override
    public final AstNode step(ExecutionContext context) {
        AstNode result = delegate.step(context);
        if(delegate.isDone()) {
            processSubExpressionResults(delegate.getSubExpressionResults(), context);
        }
        return result;
    }

    abstract void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext context);

    @Override
    public final boolean isAcceptingChildResults() {
        return true;
    }

    @Override
    public final void acceptChildResult(Object value, ExecutionContext context) {
        delegate.acceptChildResult(value);
    }
}
