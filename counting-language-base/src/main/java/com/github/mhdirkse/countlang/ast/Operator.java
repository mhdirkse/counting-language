package com.github.mhdirkse.countlang.ast;

import java.util.List;

import com.github.mhdirkse.countlang.execution.ProgramException;
import com.github.mhdirkse.countlang.execution.Value;

public abstract class Operator extends AstNode {
    public Operator(final int line, final int column) {
        super(line, column);
    }

    public abstract String getName();

    public final Value execute(final List<Value> arguments) {
        long firstArg = (long) arguments.get(0).getValue();
        long secondArg = (long) arguments.get(1).getValue();
        long longResult = executeUnchecked(firstArg, secondArg);
        if((longResult < Integer.MIN_VALUE) || (longResult > Integer.MAX_VALUE)) {
            throw new ProgramException(getLine(), getColumn(), "Overflow or underflow");
        }
        else {
            return new Value((int) longResult);
        }
    }

    abstract long executeUnchecked(long firstArg, long secondArg);

    @Override
    public void accept(final Visitor v) {
        v.visitOperator(this);
    }

    public static final class OperatorAdd extends Operator {
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

    public static final class OperatorSubtract extends Operator {
        public OperatorSubtract(final int line, final int column) {
            super(line, column);
        }

        @Override
        public String getName() {
            return "-";
        }

        @Override
        long executeUnchecked(final long first, final long second) {
            return first - second;
        }
    }

    public static final class OperatorMultiply extends Operator {
        public OperatorMultiply(final int line, final int column) {
            super(line, column);
        }

        @Override
        public String getName() {
            return "*";
        }

        @Override
        long executeUnchecked(final long first, final long second) {
            return first * second;
        }
    }

    public static final class OperatorDivide extends Operator {
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
                throw new ProgramException(getLine(), getColumn(), "Division by zero");
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
}
