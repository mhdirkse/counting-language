package com.github.mhdirkse.countlang.steps;

public interface ExecutionPoint extends Comparable<ExecutionPoint> {
    boolean isValid();
    ExecutionPoint afterFinished();
}
