package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.AFTER;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.BEFORE;

import com.github.mhdirkse.countlang.ast.AstNode;

abstract class AtomicHandler<T> implements AstNodeExecution<T> {
    private AstNodeExecutionState state;

    AtomicHandler() {
        state = BEFORE; 
    }

    @Override
    public AstNodeExecutionState getState() {
        return state;
    }

    @Override
    public AstNode step(ExecutionContext<T> context) {
        context.onResult(getValue(context));
        state = AFTER;
        return null;
    }

    abstract T getValue(ExecutionContext<T> context);

    @Override
    public boolean handleDescendantResult(T value, ExecutionContext<T> context) {
        throw new IllegalStateException("Executing non-composite AST node, so there cannot be results from children");
    }
}
