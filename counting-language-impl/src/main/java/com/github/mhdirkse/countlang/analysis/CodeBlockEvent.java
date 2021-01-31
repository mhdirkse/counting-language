package com.github.mhdirkse.countlang.analysis;

import java.util.Comparator;

import lombok.Getter;
import lombok.Setter;

class CodeBlockEvent implements Comparable<CodeBlockEvent> {
    private final @Getter int line;
    private final @Getter int column;
    static final Comparator<CodeBlockEvent> COMPARATOR = Comparator.comparingInt(CodeBlockEvent::getLine).thenComparing(CodeBlockEvent::getColumn);

    CodeBlockEvent(int line, int column) {
        this.line = line;
        this.column = column;
    }

    @Override
    public int compareTo(final CodeBlockEvent other) {
        return COMPARATOR.compare(this, other);
    }

    static class Return extends CodeBlockEvent {
        private @Getter @Setter Statement after = null;

        Return(int line, int column) {
            super(line, column);
        }
    }

    static class Statement extends CodeBlockEvent {
        Statement(int line, int column) {
            super(line, column);
        }
    }
}
