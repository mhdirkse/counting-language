package com.github.mhdirkse.countlang.ast;

public interface FunctionCallErrorHandler {
    void handleParameterCountMismatch(int numExpected, int numActual);
    void handleParameterTypeMismatch(int parameterNumber, CountlangType expectedType, CountlangType actualType);
}
