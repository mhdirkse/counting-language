package com.github.mhdirkse.countlang.ast;

import java.util.List;

public interface FunctionDefinition {
    FunctionKey getKey();
    CountlangType checkCallAndGetReturnType(List<CountlangType> arguments, FunctionCallErrorHandler errorHandler);
}
