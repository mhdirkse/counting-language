package com.github.mhdirkse.countlang.execution;

import com.github.mhdirkse.countlang.ast.RunnableFunction;
import com.github.mhdirkse.countlang.ast.Value;

public interface ExecutionContext {

    boolean hasSymbol(String name);

    Value getValue(String name);

    void putSymbol(String name, Value value);

    boolean hasFunction(String name);

    RunnableFunction getFunction(String name);

    void putFunction(final RunnableFunction function);

    void pushFrame(StackFrame frame);

    void popFrame();

    OutputStrategy getOutputStrategy();

    void setOutputStrategy(OutputStrategy outputStrategy);
}