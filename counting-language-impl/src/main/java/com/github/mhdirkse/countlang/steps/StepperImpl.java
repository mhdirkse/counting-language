package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.types.Distribution;
import com.github.mhdirkse.countlang.utils.Stack;

class StepperImpl implements Stepper, StepperCallback {
    private final Stack<Executor> executors = new Stack<>();

    StepperImpl(final AstNode target, final ExecutionContext context, final AstNodeExecutionFactory factory) {
        executors.push(new Executor(context, factory, factory.create(target)));
    }

    @Override
    public boolean hasMoreSteps() {
        if(executors.isEmpty()) {
            return false;
        }
        return executors.peek().hasMoreSteps();
    }

    @Override
    public void step() {
        if(!hasMoreSteps()) {
            throw new IllegalStateException("No more steps");
        }
        executors.peek().step();
    }

    @Override
    public Object onResult(Object value) {
        return executors.peek().onResult(value);
    }

    @Override
    public void stopFunctionCall(FunctionCallExpression functionCallExpression) {
        executors.peek().stopFunctionCall(functionCallExpression);
    }

    @Override
    public ExecutionPoint getExecutionPoint() {
        return executors.peek().getExecutionPoint();
    }

    @Override
    public void forkExecutor() {
        executors.push(new Executor(executors.peek()));
    }

    @Override
    public void stopExecutor() {
        executors.pop();
    }

    @Override
    public void startSampledVariable(Distribution sampledDistribution) {
        executors.peek().startSampledVariable(sampledDistribution);
    }

    @Override
    public void stopSampledVariable() {
        executors.peek().stopSampledVariable();
    }

    @Override
    public boolean hasNextValue() {
        return executors.peek().hasNextValue();
    }

    @Override
    public int nextValue() {
        return executors.peek().nextValue();
    }
}
