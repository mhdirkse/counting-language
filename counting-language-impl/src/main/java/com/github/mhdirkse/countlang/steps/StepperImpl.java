package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.AFTER;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import com.github.mhdirkse.countlang.ast.AstNode;

class StepperImpl<T> implements Stepper, StepperCallback<T> {
    private final Deque<AstNodeExecution<T>> callStack;
    private final ExecutionContext<T> context;
    private final AstNodeExecutionFactory<T> factory;

    StepperImpl(final AstNode target, final ExecutionContext<T> context, final AstNodeExecutionFactory<T> factory) {
        this.factory = factory;
        this.context = context;
        callStack = new ArrayDeque<>();
        callStack.addLast(factory.create(target));
    }

    @Override
    public boolean hasMoreSteps() {
        return !callStack.isEmpty();
    }

    @Override
    public void step() {
        if(!hasMoreSteps()) {
            throw new IllegalStateException("No more steps");
        }
        if(callStack.getLast().getState() == AFTER) {
            callStack.removeLast();
        }
        if(!callStack.isEmpty()) {
            AstNode child = callStack.getLast().step(context);
            if(child != null) {
                callStack.addLast(factory.create(child));
            }
        }
    }

    @Override
    public T onResult(T value) {
        Iterator<AstNodeExecution<T>> it = callStack.descendingIterator();
        it.next();
        boolean handled = false;
        while(it.hasNext() && !handled) {
            handled = it.next().handleDescendantResult(value, context);
        }
        return value;
    }
}
