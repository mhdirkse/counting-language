package com.github.mhdirkse.countlang.execution;

import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.Value;

public final class ExecutionContextImpl implements ExecutionContext {
    private final Scope scope = new Scope();
    private OutputStrategy outputStrategy = null;

    public ExecutionContextImpl(final OutputStrategy outputStrategy) {
        this.outputStrategy = outputStrategy;
    }

    /* (non-Javadoc)
     * @see com.github.mhdirkse.countlang.execution.IExecutionContext#hasSymbol(java.lang.String)
     */
    @Override
    public boolean hasSymbol(String name) {
        return scope.hasSymbol(name);
    }

    /* (non-Javadoc)
     * @see com.github.mhdirkse.countlang.execution.IExecutionContext#getValue(java.lang.String)
     */
    @Override
    public Value getValue(String name) {
        return scope.getValue(name);
    }

    /* (non-Javadoc)
     * @see com.github.mhdirkse.countlang.execution.IExecutionContext#putSymbol(java.lang.String, com.github.mhdirkse.countlang.ast.Value)
     */
    @Override
    public void putSymbol(String name, Value value) {
        scope.putSymbol(name, value);
    }

    /* (non-Javadoc)
     * @see com.github.mhdirkse.countlang.execution.IExecutionContext#pushFrame(com.github.mhdirkse.countlang.execution.StackFrame)
     */
    @Override
    public void pushFrame(StackFrame frame) {
        scope.pushFrame(frame);
    }

    /* (non-Javadoc)
     * @see com.github.mhdirkse.countlang.execution.IExecutionContext#popFrame()
     */
    @Override
    public void popFrame() {
        scope.popFrame();
    }

    /* (non-Javadoc)
     * @see com.github.mhdirkse.countlang.execution.IExecutionContext#addFunction(com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement)
     */
    @Override
    public void addFunction(final FunctionDefinitionStatement definition) {
        // Todo: Implement.
    }

    /* (non-Javadoc)
     * @see com.github.mhdirkse.countlang.execution.IExecutionContext#getOutputStrategy()
     */
    @Override
    public OutputStrategy getOutputStrategy() {
        return outputStrategy;
    }

    /* (non-Javadoc)
     * @see com.github.mhdirkse.countlang.execution.IExecutionContext#setOutputStrategy(com.github.mhdirkse.countlang.execution.OutputStrategy)
     */
    @Override
    public void setOutputStrategy(final OutputStrategy outputStrategy) {
        this.outputStrategy = outputStrategy;
    }
}
