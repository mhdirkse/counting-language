package com.github.mhdirkse.countlang.execution;

public interface OutputStrategy {
    public void output(String s);
    public void error(String s);
}
