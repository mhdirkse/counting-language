package com.github.mhdirkse.countlang.steps;

import java.util.List;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CompositeNode;

abstract class ExpressionResultsCollector<T> implements AstNodeExecution<T> {
    final CompositeNode node;
    SubExpressionStepper<T> delegate;

    ExpressionResultsCollector(final CompositeNode node) {
        this.node = node;
        delegate = new SubExpressionStepper<T>(node.getSubExpressions());
    }

    @Override
    public AstNode getAstNode() {
        return (AstNode) node;
    }

    @Override
    public AstNodeExecutionState getState() {
        return delegate.getState();
    }

    @Override
    public AstNode step(ExecutionContext<T> context) {
        AstNode result = delegate.step(context);
        if(delegate.isDone()) {
            processSubExpressionResults(delegate.getSubExpressionResults(), context);
        }
        return result;
    }

    abstract void processSubExpressionResults(List<T> subExpressionResults, ExecutionContext<T> context);

    @Override
    public boolean handleDescendantResult(T value) {
        delegate.handleDescendantResult(value);
        return isDescendantResultHandled();
    }

    abstract boolean isDescendantResultHandled();
}
