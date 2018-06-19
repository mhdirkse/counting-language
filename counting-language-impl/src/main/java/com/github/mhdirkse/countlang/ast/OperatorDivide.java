package com.github.mhdirkse.countlang.ast;

import com.github.mhdirkse.countlang.execution.ProgramRuntimeException;

public final class OperatorDivide extends Operator {
    public OperatorDivide(final int line, final int column) {
        super(line, column);
    }

    @Override
    public String getName() {
        return "/";
    }

    @Override
    long executeUnchecked(final long first, final long second) {
        if (second == 0) {
            throw new ProgramRuntimeException(getLine(), getColumn(), "Division by zero");
        }
        long result = first / second;
        if ((first % second) != 0) {
            result = correctForNonzeroRemainder(first, second, result);
        }
        return result;
    }

    private long correctForNonzeroRemainder(final long first, final long second, long result) {
        if ((first >= 0) && (second < 0)) {
            result -= 1;
        } else if ((first < 0) && (second > 0)) {
            result -= 1;
        }
        return result;
    }
}
