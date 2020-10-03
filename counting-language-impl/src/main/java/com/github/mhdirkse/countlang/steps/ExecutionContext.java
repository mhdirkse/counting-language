package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.utils.AbstractStatusCode;

interface ExecutionContext<T> extends StepperCallback<T> {
    T readSymbol(String symbol);
    void writeSymbol(T value);
    void report(AbstractStatusCode statusCode, int line, int column, String... others);
}
