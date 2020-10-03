package com.github.mhdirkse.countlang.steps;

interface StepperCallback<T> {
    T onResult(T value);
}
