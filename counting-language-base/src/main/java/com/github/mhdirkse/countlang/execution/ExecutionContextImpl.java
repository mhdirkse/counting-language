package com.github.mhdirkse.countlang.execution;

public final class ExecutionContextImpl implements ExecutionContext {
    private final Scope scope = new Scope();
    private final FunctionDefinitions functions = new FunctionDefinitions();
    private OutputStrategy outputStrategy = null;
    private StackFrame newStackFrame = null;

    public ExecutionContextImpl(final OutputStrategy outputStrategy) {
        this.outputStrategy = outputStrategy;
    }

    @Override
    public boolean hasSymbol(String name) {
        return scope.hasSymbol(name);
    }

    @Override
    public Object getValue(String name) {
        return scope.getValue(name);
    }

    @Override
    public void putSymbol(String name, Object value) {
        scope.putSymbol(name, value);
    }

    @Override
    public void putSymbolInNewFrame(String name, Object value) {
    	newStackFrame.putSymbol(name, value);
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
    public void startPreparingNewFrame() {
    	newStackFrame = new StackFrame();
    }

    @Override
    public void pushNewFrame() {
        scope.pushFrame(newStackFrame);
    }

    @Override
    public void popFrame() {
        scope.popFrame();
    }

    @Override
    public void output(final String result) {
        outputStrategy.output(result);
    }
}
