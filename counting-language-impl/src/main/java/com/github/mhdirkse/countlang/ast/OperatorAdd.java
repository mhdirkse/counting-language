package com.github.mhdirkse.countlang.ast;

public final class OperatorAdd extends Operator {
    public OperatorAdd(final int line, final int column) {
        super(line, column);
    }

    @Override
    public String getName() {
        return "+";
    }

    @Override
    long executeUnchecked(final long first, final long second) {
        return first + second;
    }
}
