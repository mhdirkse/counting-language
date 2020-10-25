package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.AFTER;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import com.github.mhdirkse.countlang.ast.AstNode;

class StepperImpl implements Stepper, StepperCallback {
    private final AstNode target;
    private final Deque<AstNodeExecution> callStack;
    private final ExecutionContext context;
    private final AstNodeExecutionFactory factory;

    StepperImpl(final AstNode target, final ExecutionContext context, final AstNodeExecutionFactory factory) {
        this.target = target;
        this.factory = factory;
        this.context = context;
        callStack = new ArrayDeque<>();
    }

    void init() {
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
    public Object onResult(Object value) {
        Iterator<AstNodeExecution> it = callStack.descendingIterator();
        it.next();
        boolean handled = false;
        while(it.hasNext() && !handled) {
            handled = it.next().handleDescendantResult(value, context);
        }
        return value;
    }
}
