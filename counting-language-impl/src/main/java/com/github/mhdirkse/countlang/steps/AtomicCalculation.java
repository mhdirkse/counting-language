package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.AFTER;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.BEFORE;

import com.github.mhdirkse.countlang.ast.AstNode;

abstract class AtomicCalculation implements AstNodeExecution {
    private AstNodeExecutionState state;

    AtomicCalculation() {
        state = BEFORE; 
    }

    @Override
    public AstNodeExecutionState getState() {
        return state;
    }

    @Override
    public AstNode step(ExecutionContext context) {
        context.onResult(getValue(context));
        state = AFTER;
        return null;
    }

    abstract Object getValue(ExecutionContext context);

    @Override
    public boolean handleDescendantResult(Object value, ExecutionContext context) {
        throw new IllegalStateException("Executing non-composite AST node, so there cannot be results from children");
    }
}
