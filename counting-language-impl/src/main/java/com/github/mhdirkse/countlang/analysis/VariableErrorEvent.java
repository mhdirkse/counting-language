package com.github.mhdirkse.countlang.analysis;

import com.github.mhdirkse.countlang.tasks.StatusReporter;

class VariableErrorEvent {
    static enum Kind {
        DOES_NOT_EXIST,
        DUPLICATE_PARAMETER;
    }

    private final Kind kind;
    private final String name;
    private final int line;
    private final int column;

    VariableErrorEvent(Kind kind, String name, int line, int column) {
        this.kind = kind;
        this.name = name;
        this.line = line;
        this.column = column;
    }

    void report(StatusReporter reporter) {
        // TODO: Implement.
    }
}
