package com.github.mhdirkse.countlang.steps;

public interface Stepper {
    boolean hasMoreSteps();
    void step();

    default void run() {
        while(hasMoreSteps() ) {
            step();
        }
    }
}
