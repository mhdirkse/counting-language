/*
 * Copyright Martijn Dirkse 2020
 *
 * This file is part of counting-language.
 *
 * counting-language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.mhdirkse.countlang.ast;

import java.util.List;

public abstract class Operator extends AstNode {
    public Operator(final int line, final int column) {
        super(line, column);
    }

    public abstract Object execute(final List<Object> arguments);
    public abstract String getName();
    public abstract int getNumArguments();
    
    /**
     * @param argumentTypes Types of arguments. It is assumed that the length matches getNumArguments(). 
     * @return true if the type could be established, false if the argument types are not compatible.
     */
    public abstract boolean checkAndEstablishTypes(List<CountlangType> argumentTypes);

    /**
     * Assumes that checkAndEstablishTypes has been called.
     * @return The result type of the operator.
     */
    public abstract CountlangType getResultType();

    @Override
    public void accept(final Visitor v) {
        v.visitOperator(this);
    }

    public static abstract class IntegerOperator extends Operator {
        public IntegerOperator(final int line, final int column) {
            super(line, column);
        }

        @Override
        public final Object execute(final List<Object> arguments) {
            long longResult = getLongResult(arguments);
            if((longResult < Integer.MIN_VALUE) || (longResult > Integer.MAX_VALUE)) {
                throw new ProgramException(getLine(), getColumn(), "Overflow or underflow");
            }
            else {
                return Integer.valueOf((int) longResult);
            }
        }

        abstract long getLongResult(final List<Object> arguments);        
    }

    public static final class OperatorUnaryMinus extends IntegerOperator {
    	public OperatorUnaryMinus(final int line, final int column) {
    		super(line, column);
    	}

    	@Override
    	public final String getName() {
    		return "unaryMinus";
    	}

    	@Override
    	final long getLongResult(final List<Object> arguments) {
    		long arg = (Integer) arguments.get(0);
    		return -arg;
    	}

    	@Override
    	public final int getNumArguments() {
    	    return 1;
    	}

    	@Override
    	public final boolean checkAndEstablishTypes(final List<CountlangType> argumentTypes) {
    	    return argumentTypes.get(0) == CountlangType.INT;
    	}

    	@Override
    	public final CountlangType getResultType() {
    	    return CountlangType.INT;
    	}
    }

    private static abstract class BinaryOperator extends IntegerOperator {
    	BinaryOperator(final int line, final int column) {
    		super(line, column);
    	}

    	@Override
    	final long getLongResult(final List<Object> arguments) {
    		long firstArg = (Integer) arguments.get(0);
            long secondArg = (Integer) arguments.get(1);
            return executeUnchecked(firstArg, secondArg);
    	}

    	@Override
    	public final int getNumArguments() {
    	    return 2;
    	}

    	@Override
    	public final boolean checkAndEstablishTypes(final List<CountlangType> argumentTypes) {
    	    return (argumentTypes.get(0) == CountlangType.INT)
    	            && (argumentTypes.get(1) == CountlangType.INT);
    	}

    	@Override
    	public final CountlangType getResultType() {
    	    return CountlangType.INT;
    	}

        abstract long executeUnchecked(long firstArg, long secondArg);
    }

    public static final class OperatorAdd extends BinaryOperator {
        public OperatorAdd(final int line, final int column) {
            super(line, column);
        }

        @Override
        public final String getName() {
            return "+";
        }

        @Override
        final long executeUnchecked(final long first, final long second) {
            return first + second;
        }
    }

    public static final class OperatorSubtract extends BinaryOperator {
        public OperatorSubtract(final int line, final int column) {
            super(line, column);
        }

        @Override
        public final String getName() {
            return "-";
        }

        @Override
        final long executeUnchecked(final long first, final long second) {
            return first - second;
        }
    }

    public static final class OperatorMultiply extends BinaryOperator {
        public OperatorMultiply(final int line, final int column) {
            super(line, column);
        }

        @Override
        public final String getName() {
            return "*";
        }

        @Override
        final long executeUnchecked(final long first, final long second) {
            return first * second;
        }
    }

    public static final class OperatorDivide extends BinaryOperator {
        public OperatorDivide(final int line, final int column) {
            super(line, column);
        }

        @Override
        public final String getName() {
            return "/";
        }

        @Override
        final long executeUnchecked(final long first, final long second) {
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

    public static class OperatorNot extends Operator {
        public OperatorNot(int line, int column) {
            super(line, column);
        }

        @Override
        public final String getName() {
            return "not";
        }

        @Override
        public final CountlangType getResultType() {
            return CountlangType.BOOL;
        }

        @Override
        public final int getNumArguments() {
            return 1;
        }

        @Override
        public final boolean checkAndEstablishTypes(List<CountlangType> argumentTypes) {
            return argumentTypes.get(0) == CountlangType.BOOL;
        }

        @Override
        public final Object execute(List<Object> arguments) {
            boolean input = (Boolean) arguments.get(0);
            return Boolean.valueOf(!input);
        }
    }

    public static abstract class BinBoolOperator extends Operator {
        BinBoolOperator(final int line, final int column) {
            super(line, column);
        }

        @Override
        public final CountlangType getResultType() {
            return CountlangType.BOOL;
        }

        @Override
        public final int getNumArguments() {
            return 2;
        }

        @Override
        public final boolean checkAndEstablishTypes(List<CountlangType> argumentTypes) {
            return (argumentTypes.get(0) == CountlangType.BOOL)
                    && (argumentTypes.get(1) == CountlangType.BOOL);
        }        
    }

    public static final class OperatorAnd extends BinBoolOperator {
        public OperatorAnd(final int line, final int column) {
            super(line, column);
        }

        @Override
        public final String getName() {
            return "and";
        }

        @Override
        public final Object execute(List<Object> arguments) {
            boolean i1 = (Boolean) arguments.get(0);
            boolean i2 = (Boolean) arguments.get(1);
            return Boolean.valueOf(i1 && i2);
        }
    }

    public static final class OperatorOr extends BinBoolOperator {
        public OperatorOr(final int line, final int column) {
            super(line, column);
        }

        @Override
        public final String getName() {
            return "or";
        }

        @Override
        public final Object execute(List<Object> arguments) {
            boolean i1 = (Boolean) arguments.get(0);
            boolean i2 = (Boolean) arguments.get(1);
            return Boolean.valueOf(i1 || i2);
        }
    }

    public static abstract class RelOp extends Operator {
        RelOp(final int line, final int column) {
            super(line, column);
        }

        @Override
        public final int getNumArguments() {
            return 2;
        }

        @Override
        public final boolean checkAndEstablishTypes(List<CountlangType> argumentTypes) {
            return (argumentTypes.get(0) == CountlangType.INT)
                    && (argumentTypes.get(1) == CountlangType.INT);
        }

        @Override
        public final CountlangType getResultType() {
            return CountlangType.BOOL;
        }

        @Override
        public final Object execute(List<Object> arguments) {
            int i1 = (Integer) arguments.get(0);
            int i2 = (Integer) arguments.get(1);
            return Boolean.valueOf(executeInt(i1, i2));
        }

        abstract boolean executeInt(final int i1, final int i2);
    }

    public static final class OperatorLessThan extends RelOp {
        public OperatorLessThan(final int line, final int column) {
            super(line, column);
        }

        @Override
        public final String getName() {
            return "<";
        }

        @Override
        final boolean executeInt(final int i1, final int i2) {
            return i1 < i2;
        }
    }

    public static final class OperatorLessEqual extends RelOp {
        public OperatorLessEqual(final int line, final int column) {
            super(line, column);
        }

        @Override
        public final String getName() {
            return "<=";
        }

        @Override
        final boolean executeInt(final int i1, final int i2) {
            return i1 <= i2;
        }
    }

    public static final class OperatorGreaterThan extends RelOp {
        public OperatorGreaterThan(final int line, final int column) {
            super(line, column);
        }

        @Override
        public final String getName() {
            return ">";
        }

        @Override
        final boolean executeInt(final int i1, final int i2) {
            return i1 > i2;
        }
    }

    public static final class OperatorGreaterEqual extends RelOp {
        public OperatorGreaterEqual(final int line, final int column) {
            super(line, column);
        }

        @Override
        public final String getName() {
            return ">=";
        }

        @Override
        final boolean executeInt(final int i1, final int i2) {
            return i1 >= i2;
        }
    }

    public static abstract class MultiTypeRelOp extends Operator {
        CountlangType argType = CountlangType.UNKNOWN;

        MultiTypeRelOp(final int line, final int column) {
            super(line, column);
        }

        @Override
        public final int getNumArguments() {
            return 2;
        }

        @Override
        public final boolean checkAndEstablishTypes(List<CountlangType> argumentTypes) {
            boolean result = (argumentTypes.get(0) == argumentTypes.get(1))
                    && (!argumentTypes.get(0).equals(CountlangType.DISTRIBUTION));
            if(result) {
                argType = argumentTypes.get(0);
            }
            return result;
        }

        @Override
        public final CountlangType getResultType() {
            return CountlangType.BOOL;
        }

        @Override
        public final Object execute(List<Object> arguments) {
            boolean result = false; 
            switch(argType) {
            case BOOL:
                boolean b1 = (Boolean) arguments.get(0);
                boolean b2 = (Boolean) arguments.get(1);
                result = applyBool(b1, b2);
                break;
            case INT:
                int i1 = (Integer) arguments.get(0);
                int i2 = (Integer) arguments.get(1);
                result = applyInt(i1, i2);
                break;
            case UNKNOWN:
            case DISTRIBUTION:
                throw new IllegalStateException("Cannot execute when type checking failed");
            }
            return Boolean.valueOf(result);
        }        

        abstract boolean applyInt(final int i1, final int i2);
        abstract boolean applyBool(final boolean b1, final boolean b2);
    }

    public static final class OperatorEquals extends MultiTypeRelOp {
        public OperatorEquals(final int line, final int column) {
            super(line, column);
        }

        @Override
        public final String getName() {
            return "==";
        }

        @Override
        final boolean applyInt(final int i1, final int i2) {
            return i1 == i2;
        }

        @Override
        final boolean applyBool(final boolean b1, final boolean b2) {
            return b1 == b2;
        }
    }

    public static final class OperatorNotEquals extends MultiTypeRelOp {
        public OperatorNotEquals(final int line, final int column) {
            super(line, column);
        }

        @Override
        public final String getName() {
            return "!=";
        }

        @Override
        final boolean applyInt(final int i1, final int i2) {
            return i1 != i2;
        }

        @Override
        final boolean applyBool(final boolean b1, final boolean b2) {
            return b1 != b2;
        }
    }
}
