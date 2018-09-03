package com.github.mhdirkse.countlang.execution;

import java.util.List;

public abstract class Operator {
    private final int line;
    private final int column;

    int getLine() {
        return line;
    }

    int getColumn() {
        return column;
    }

    public Operator(final int line, final int column) {
        this.line = line;
        this.column = column;
    }

    public abstract String getName();

    public final Value execute(final List<Value> arguments) {
        long firstArg = (long) arguments.get(0).getValue();
        long secondArg = (long) arguments.get(1).getValue();
        long longResult = executeUnchecked(firstArg, secondArg);
        if((longResult < Integer.MIN_VALUE) || (longResult > Integer.MAX_VALUE)) {
            throw new ProgramRuntimeException(line, column, "Overflow or underflow");
        }
        else {
            return new Value((int) longResult);
        }
    }

    abstract long executeUnchecked(long firstArg, long secondArg);
}
