package com.github.mhdirkse.countlang.ast;

public final class ExecutionContext {
    private Scope scope = null;
    private OutputStrategy outputStrategy = null;

    public ExecutionContext(final Scope scope, final OutputStrategy outputStrategy) {
        this.scope = scope;
        this.outputStrategy = outputStrategy;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(final Scope scope) {
        this.scope = scope;
    }

    public OutputStrategy getOutputStrategy() {
        return outputStrategy;
    }

    public void setOutputStrategy(final OutputStrategy outputStrategy) {
        this.outputStrategy = outputStrategy;
    }
}
