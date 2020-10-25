package com.github.mhdirkse.countlang.tasks;

import com.github.mhdirkse.utils.AbstractStatusCode;

public interface StatusReporter {
    void report(AbstractStatusCode statusCode, int line, int column, String... others);
    boolean hasErrors();
}
