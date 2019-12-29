package com.github.mhdirkse.countlang.execution;

public interface OutputStrategy {
    public void output(String s);
    public void error(String s);

    public static final OutputStrategy NO_OUTPUT = new OutputStrategy() {
        @Override
        public void output(String s) {}
        @Override
        public void error(String s) {}
    };
}
