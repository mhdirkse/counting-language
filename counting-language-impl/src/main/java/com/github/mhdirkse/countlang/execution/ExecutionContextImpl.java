package com.github.mhdirkse.countlang.execution;

import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.StackFrameAccess;

public final class ExecutionContextImpl implements ExecutionContext {
    private final Scope scope = new Scope();
    private final FunctionDefinitions functions = new FunctionDefinitions();
    private OutputStrategy outputStrategy = null;
    private StackFrame newStackFrame = null;
    private ValueStack valueStack = new ValueStack();

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
    public CountlangType getType(String name) {
        return scope.getCountlangType(name);
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
    public FunctionDefinitionStatement getFunction(String name) {
        return functions.getFunction(name);
    }

    @Override
    public void putFunction(final FunctionDefinitionStatement function) {
        functions.putFunction(function);
    }

    @Override
    public void startPreparingNewFrame(final StackFrameAccess stackFrameAccess) {
    	newStackFrame = new StackFrame(stackFrameAccess);
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

    @Override
    public void pushValue(final Object value) {
        valueStack.push(value);
    }

    @Override
    public Object popValue() {
        return valueStack.pop();
    }
}
