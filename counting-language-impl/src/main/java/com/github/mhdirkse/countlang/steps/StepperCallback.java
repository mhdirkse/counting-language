package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.execution.SampleContextBase;

interface StepperCallback extends SampleContextBase {
    Object onResult(Object value);
    void stopFunctionCall(FunctionCallExpression functionCallExpression);
    void forkExecutor();
    void stopExecutor();
}
