package com.github.mhdirkse.countlang.steps;

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
    public AstNode getAstNode() {
        return (AstNode) node;
    }

    @Override
    public AstNodeExecutionState getState() {
        return delegate.getState();
    }

    @Override
    public AstNode step(ExecutionContext context) {
        AstNode result = delegate.step(context);
        if(delegate.isDone()) {
            processSubExpressionResults(delegate.getSubExpressionResults(), context);
        }
        return result;
    }

    abstract void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext context);

    @Override
    public final boolean handleDescendantResult(Object value, ExecutionContext context) {
        delegate.handleDescendantResult(value);
        return isDescendantResultHandled();
    }

    abstract boolean isDescendantResultHandled();
}
