package com.github.mhdirkse.countlang.ast;

public final class ExecutionContext {
    private final Scope scope = new Scope();
    private OutputStrategy outputStrategy = null;

    public ExecutionContext(final OutputStrategy outputStrategy) {
        this.outputStrategy = outputStrategy;
    }

    public boolean hasSymbol(String name) {
        return scope.hasSymbol(name);
    }

    public Value getValue(String name) {
        return scope.getValue(name);
    }

    public void putSymbol(String name, Value value) {
        scope.putSymbol(name, value);
    }

    public OutputStrategy getOutputStrategy() {
        return outputStrategy;
    }

    public void setOutputStrategy(final OutputStrategy outputStrategy) {
        this.outputStrategy = outputStrategy;
    }
}
