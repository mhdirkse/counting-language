package com.github.mhdirkse.countlang.ast;

public interface OutputStrategy {
    public void output(String s);
    public void error(String s);
}
