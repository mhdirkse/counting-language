package com.github.mhdirkse.countlang.ast;

import java.util.List;

import com.github.mhdirkse.countlang.execution.ExecutionContext;

public interface RunnableFunction {
    String getName();
    Value runFunction(List<Expression> actualParameters, ExecutionContext ctx);
}