package com.github.mhdirkse.countlang.analysis;

import lombok.Getter;

class AccessEvent {
    enum Type {
        READ,
        WRITE,
        DEFINE
    }

    private final @Getter Type eventType;
    private final @Getter String symbol;
    private final @Getter int line;
    private final @Getter int column;

    AccessEvent(Type eventType, String symbol, int line, int column) {
        this.eventType = eventType;
        this.symbol = symbol;
        this.line = line;
        this.column = column;
    }
}
