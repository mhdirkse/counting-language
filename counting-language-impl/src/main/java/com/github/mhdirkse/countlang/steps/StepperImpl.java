package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.AFTER;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;

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
        nextChildAcceptor(it).ifPresent(acc -> acc.acceptChildResult(value, context));
        return value;
    }

    private Optional<AstNodeExecution> nextChildAcceptor(Iterator<AstNodeExecution> it) {
        while(it.hasNext()) {
            AstNodeExecution current = it.next();
            if(current.isAcceptingChildResults()) {
                return Optional.of(current);
            }
        }
        return Optional.empty();
    }

    @Override
    public void stopFunctionCall(FunctionCallExpression functionCallExpression) {
        Iterator<AstNodeExecution> it = callStack.descendingIterator();
        AstNodeExecution currentExecution = it.next();
        // We do not check on reaching the end of the callStack, but on finding the function we want to stop.
        while(currentExecution.getAstNode() != functionCallExpression) {
            if(currentExecution instanceof StatementGroupCalculation) {
                ((StatementGroupCalculation) currentExecution).stopFunctionCall();
            }
            currentExecution = it.next();
        }
    }

    @Override
    public ExecutionPoint getExecutionPoint() {
        List<ExecutionPointNode> nodes = new ArrayList<>();
        callStack.forEach(node -> nodes.add(node.getExecutionPointNode()));
        return new ExecutionPointImpl(nodes);
    }
}
