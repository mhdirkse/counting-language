package com.github.mhdirkse.countlang.execution;

public interface ExecutionContext {

    boolean hasSymbol(String name);

    Value getValue(String name);

    void putSymbol(String name, Value value);

    void putSymbolInNewFrame(String name, Value value);

    boolean hasFunction(String name);

    RunnableFunction getFunction(String name);

    void putFunction(final RunnableFunction function);

    void startPreparingNewFrame();

    void pushNewFrame();

    void popFrame();

    void output(String result);
}