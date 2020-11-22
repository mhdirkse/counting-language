package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.FunctionCallExpression;

interface StepperCallback {
    Object onResult(Object value);
    void stopFunctionCall(FunctionCallExpression functionCallExpression);
    void forkExecutor();
    void stopExecutor();
}
