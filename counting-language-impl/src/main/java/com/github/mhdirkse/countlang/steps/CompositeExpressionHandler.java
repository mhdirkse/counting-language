package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.AFTER;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.BEFORE;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.RUNNING;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CompositeExpression;

abstract class CompositeExpressionHandler<T> implements AstNodeExecution<T> {
    final CompositeExpression node;
    private AstNodeExecutionState state;
    private int subExpression = 0;
    List<T> subExpressionResults = new ArrayList<>();

    CompositeExpressionHandler(final CompositeExpression node) {
        this.node = node;
        this.state = BEFORE;
    }

    @Override
    public AstNode getAstNode() {
        return node;
    }

    @Override
    public AstNodeExecutionState getState() {
        return state;
    }

    @Override
    public AstNode step(ExecutionContext<T> context) {
        state = RUNNING;
        if(subExpression < node.getNumSubExpressions()) {
            return node.getSubExpression(subExpression++);
        }
        else {
            context.onResult(processSubExpressionResults(context));
            state = AFTER;
            return null;
        }
    }

    abstract T processSubExpressionResults(ExecutionContext<T> context);

    @Override
    public boolean handleDescendantResult(T value) {
        subExpressionResults.add(value);
        return true;
    }
}
