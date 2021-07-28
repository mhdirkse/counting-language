package com.github.mhdirkse.countlang.ast;

public interface FunctionDefinition {
    FunctionKey getKey();
    CountlangType getReturnType();
    int getNumParameters();
    CountlangType getFormalParameterType(int i);
}
