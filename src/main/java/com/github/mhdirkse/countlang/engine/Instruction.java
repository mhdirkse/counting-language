package com.github.mhdirkse.countlang.engine;

public abstract class Instruction {
    abstract void run(Stack stack);

    @Override
    public final String toString() {
        return getClass().getSimpleName();
    }
}
