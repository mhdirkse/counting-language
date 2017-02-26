package com.github.mhdirkse.countlang.tasks;

public interface OutputContext {
    void output(String msg);
    void error(String msg);
}
