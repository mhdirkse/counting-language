package com.github.mhdirkse.countlang.execution;

public class DummyValue {
    private static final DummyValue instance = new DummyValue();

    public static DummyValue getInstance() {
        return instance;
    }

    private DummyValue() {}
}
