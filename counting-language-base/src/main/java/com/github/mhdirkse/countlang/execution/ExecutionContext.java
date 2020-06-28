package com.github.mhdirkse.countlang.execution;

public interface ExecutionContext {

    boolean hasSymbol(String name);

    Object getValue(String name);

    CountlangType getType(String name);

    void putSymbol(String name, Object value);

    void putSymbolInNewFrame(String name, Object value);

    boolean hasFunction(String name);

    RunnableFunction getFunction(String name);

    void putFunction(final RunnableFunction function);

    void startPreparingNewFrame(StackFrameAccess stackFrameAccess);

    void pushNewFrame();

    void popFrame();

    void output(String result);

    void pushValue(Object value);
    
    Object popValue();

    void pushNewReturnContext(final int line, final int column, boolean withReturnValue);

    void popReturnContextNoValue();

    Object popReturnContextValue();

    void setReturnValue(final Object value);
}