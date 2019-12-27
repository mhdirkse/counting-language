package com.github.mhdirkse.countlang.execution;

import java.util.List;

public interface RunnableFunction {
    String getName();
    Object runFunction(List<? extends Expression> actualParameters, ExecutionContext ctx);
}