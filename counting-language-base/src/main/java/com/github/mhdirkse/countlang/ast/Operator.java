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
        long longResult = getLongResult(arguments);
        if((longResult < Integer.MIN_VALUE) || (longResult > Integer.MAX_VALUE)) {
            throw new ProgramException(getLine(), getColumn(), "Overflow or underflow");
        }
        else {
            return new Value((int) longResult);
        }
    }

    abstract long getLongResult(final List<Value> arguments);

    @Override
    public void accept(final Visitor v) {
        v.visitOperator(this);
    }

    public static final class OperatorUnaryMinus extends Operator {
    	public OperatorUnaryMinus(final int line, final int column) {
    		super(line, column);
    	}

    	@Override
    	public String getName() {
    		return "unaryMinus";
    	}

    	@Override
    	long getLongResult(final List<Value> arguments) {
    		long arg = (long) arguments.get(0).getValue();
    		return -arg;
    	}
    }

    private static abstract class BinaryOperator extends Operator {
    	BinaryOperator(final int line, final int column) {
    		super(line, column);
    	}

    	@Override
    	final long getLongResult(final List<Value> arguments) {
    		long firstArg = (long) arguments.get(0).getValue();
            long secondArg = (long) arguments.get(1).getValue();
            return executeUnchecked(firstArg, secondArg);
    	}

        abstract long executeUnchecked(long firstArg, long secondArg);
    }

    public static final class OperatorAdd extends BinaryOperator {
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

    public static final class OperatorSubtract extends BinaryOperator {
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

    public static final class OperatorMultiply extends BinaryOperator {
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

    public static final class OperatorDivide extends BinaryOperator {
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
