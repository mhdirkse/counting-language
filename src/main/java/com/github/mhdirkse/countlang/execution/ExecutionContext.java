package com.github.mhdirkse.countlang.execution;

import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.Value;

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

    public void pushFrame(StackFrame frame) {
        scope.pushFrame(frame);
    }

    public void popFrame() {
        scope.popFrame();
    }

    public void addFunction(final FunctionDefinitionStatement definition) {
        // Todo: Implement.
    }

    public OutputStrategy getOutputStrategy() {
        return outputStrategy;
    }

    public void setOutputStrategy(final OutputStrategy outputStrategy) {
        this.outputStrategy = outputStrategy;
    }
}
