package com.github.mhdirkse.countlang.analysis;

import java.util.Comparator;

import lombok.Getter;

abstract class VariableWriteStatus implements Comparable<VariableWriteStatus> {
    enum Type {
        NEW,
        USED,
        OVERWRITTEN
    }

    private static final Comparator<VariableWriteStatus> COMPARATOR_LINE = Comparator.comparingInt(status -> status.getOriginalWrite().getLine());
    private static final Comparator<VariableWriteStatus> COMPARATOR_COLUMN = Comparator.comparingInt(status -> status.getOriginalWrite().getColumn());
    private static final Comparator<VariableWriteStatus> COMPARATOR_TYPE = Comparator.comparing(status -> status.getType());

    private static int compareOverwrite(VariableWriteStatus first, VariableWriteStatus second) {
        int result = COMPARATOR_TYPE.compare(first, second);
        if(result == 0 && (first.getType() == Type.OVERWRITTEN)) {
            result = VariableWriteStatusOverwritten.COMPARATOR.compare(
                    (VariableWriteStatusOverwritten) first, (VariableWriteStatusOverwritten) second);
        }
        return result;
    }

    private static final Comparator<VariableWriteStatus> COMPARATOR =
            COMPARATOR_LINE
            .thenComparing(COMPARATOR_COLUMN)
            .thenComparing(VariableWriteStatus::compareOverwrite);

    private final @Getter AccessEvent originalWrite;

    VariableWriteStatus(AccessEvent originalWrite) {
        this.originalWrite = originalWrite;
    }

    abstract Type getType();

    @Override
    public int compareTo(VariableWriteStatus other) {
        return COMPARATOR.compare(this, other);
    }

    static class VariableWriteStatusNew extends VariableWriteStatus {
        VariableWriteStatusNew(AccessEvent originalWrite) {
            super(originalWrite);
        }

        @Override
        Type getType() {
            return Type.NEW;
        }
    }

    static class VariableWriteStatusUsed extends VariableWriteStatus {
        VariableWriteStatusUsed(AccessEvent event) {
            super(event);
        }

        @Override
        Type getType() {
            return Type.USED;
        }
    }

    static class VariableWriteStatusOverwritten extends VariableWriteStatus {
        private static final Comparator<VariableWriteStatusOverwritten> COMPARATOR_LINE
                = Comparator.comparingInt(status -> status.getLineOverwritten());
        private static final Comparator<VariableWriteStatusOverwritten> COMPARATOR_COLUMN
                = Comparator.comparingInt(status -> status.getColumnOverwritten());
        static final Comparator<VariableWriteStatusOverwritten> COMPARATOR = COMPARATOR_LINE.thenComparing(COMPARATOR_COLUMN);
        
        private final @Getter int lineOverwritten;
        private final @Getter int columnOverwritten;

        VariableWriteStatusOverwritten(AccessEvent event, int lineOverwritten, int columnOverwritten) {
            super(event);
            this.lineOverwritten = lineOverwritten;
            this.columnOverwritten = columnOverwritten;
        }

        @Override
        Type getType() {
            return Type.OVERWRITTEN;
        }
    }
}
