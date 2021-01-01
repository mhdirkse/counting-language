package com.github.mhdirkse.countlang.analysis;

import com.github.mhdirkse.countlang.ast.CountlangType;

import lombok.Getter;

class ControlEvent {
    static enum Type {
        SWITCH_OPEN,
        SWITCH_CLOSE,
        BRANCH_OPEN,
        BRANCH_CLOSE,
        REPETITION_START,
        REPETITION_STOP,
        RETURN,
        STATEMENT_OPEN,
        STATEMENT_CLOSE
    }

    private @Getter Type eventType;
    private @Getter int line;
    private @Getter int column;
    private @Getter CountlangType countlangType;

    private ControlEvent(Type eventType, int line, int column, CountlangType countlangType) {
        this.eventType = eventType;
        this.line = line;
        this.column = column;
        this.countlangType = countlangType;
    }

    static ControlEvent controlEvent(Type eventType, int line, int column) {
        switch(eventType) {
        case SWITCH_OPEN:
        case SWITCH_CLOSE:
        case BRANCH_OPEN:
        case BRANCH_CLOSE:
        case REPETITION_START:
        case REPETITION_STOP:
        case STATEMENT_OPEN:
        case STATEMENT_CLOSE:
            return new ControlEvent(eventType, line, column, null);
        default:
            throw new IllegalArgumentException(String.format("Event type [%s] is not a control event", eventType.toString()));
        }
    }

    static ControlEvent returnEvent(CountlangType countlangType, int line, int column) {
        return new ControlEvent(Type.RETURN, line, column, countlangType);
    }
}
