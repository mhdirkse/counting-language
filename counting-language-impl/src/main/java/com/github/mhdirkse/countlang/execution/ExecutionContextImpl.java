package com.github.mhdirkse.countlang.execution;

public final class ExecutionContextImpl implements ExecutionContext {
    private final Scope scope = new Scope();
    private final FunctionDefinitions functions = new FunctionDefinitions();
    private OutputStrategy outputStrategy = null;

    public ExecutionContextImpl(final OutputStrategy outputStrategy) {
        this.outputStrategy = outputStrategy;
    }

    @Override
    public boolean hasSymbol(String name) {
        return scope.hasSymbol(name);
    }

    @Override
    public Value getValue(String name) {
        return scope.getValue(name);
    }

    @Override
    public void putSymbol(String name, Value value) {
        scope.putSymbol(name, value);
    }

    @Override
    public boolean hasFunction(String name) {
        return functions.hasFunction(name);
    }

    @Override
    public RunnableFunction getFunction(String name) {
        return functions.getFunction(name);
    }

    @Override
    public void putFunction(final RunnableFunction function) {
        functions.putFunction(function);
    }

    @Override
    public void pushFrame(StackFrame frame) {
        scope.pushFrame(frame);
    }

    @Override
    public void popFrame() {
        scope.popFrame();
    }

    @Override
    public OutputStrategy getOutputStrategy() {
        return outputStrategy;
    }

    @Override
    public void setOutputStrategy(final OutputStrategy outputStrategy) {
        this.outputStrategy = outputStrategy;
    }
}
