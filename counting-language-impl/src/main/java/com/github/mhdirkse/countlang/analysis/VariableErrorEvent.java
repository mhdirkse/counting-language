package com.github.mhdirkse.countlang.analysis;

import lombok.Getter;

class VariableErrorEvent {
    static enum Kind {
        DOES_NOT_EXIST,
        DUPLICATE_PARAMETER;
    }

    private final @Getter Kind kind;
    private final @Getter String name;
    private final @Getter int line;
    private final @Getter int column;

    VariableErrorEvent(Kind kind, String name, int line, int column) {
        this.kind = kind;
        this.name = name;
        this.line = line;
        this.column = column;
    }
}
