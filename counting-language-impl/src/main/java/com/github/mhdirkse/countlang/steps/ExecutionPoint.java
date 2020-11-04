package com.github.mhdirkse.countlang.steps;

public interface ExecutionPoint {
    boolean isValid();
    boolean isEmpty();
    ExecutionPoint afterFinished();
}
