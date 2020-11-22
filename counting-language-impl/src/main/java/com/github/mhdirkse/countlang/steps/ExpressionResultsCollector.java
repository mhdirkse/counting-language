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

    ExpressionResultsCollector(final ExpressionResultsCollector orig) {
        this.node = orig.node;
        this.delegate = new SubExpressionStepper(orig.delegate);
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
