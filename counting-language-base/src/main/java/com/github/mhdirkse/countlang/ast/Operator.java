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

import java.math.BigInteger;
import java.util.List;

import com.github.mhdirkse.countlang.algorithm.Distribution;

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

    public static final class OperatorUnaryMinus extends Operator {
    	public OperatorUnaryMinus(final int line, final int column) {
    		super(line, column);
    	}

    	@Override
    	public final String getName() {
    		return "unaryMinus";
    	}

    	@Override
    	public final Object execute(final List<Object> arguments) {
    		return ((BigInteger) arguments.get(0)).negate();
    	}

    	@Override
    	public final int getNumArguments() {
    	    return 1;
    	}

    	@Override
    	public final boolean checkAndEstablishTypes(final List<CountlangType> argumentTypes) {
    	    return argumentTypes.get(0) == CountlangType.integer();
    	}

    	@Override
    	public final CountlangType getResultType() {
    	    return CountlangType.integer();
    	}
    }

    private static abstract class BinaryOperator extends Operator {
    	BinaryOperator(final int line, final int column) {
    		super(line, column);
    	}

    	@Override
    	public final Object execute(final List<Object> arguments) {
    		BigInteger firstArg = (BigInteger) arguments.get(0);
            BigInteger secondArg = (BigInteger) arguments.get(1);
            return executeUnchecked(firstArg, secondArg);
    	}

    	@Override
    	public final int getNumArguments() {
    	    return 2;
    	}

    	@Override
    	public final boolean checkAndEstablishTypes(final List<CountlangType> argumentTypes) {
    	    return (argumentTypes.get(0) == CountlangType.integer())
    	            && (argumentTypes.get(1) == CountlangType.integer());
    	}

    	@Override
    	public final CountlangType getResultType() {
    	    return CountlangType.integer();
    	}

        abstract BigInteger executeUnchecked(BigInteger firstArg, BigInteger secondArg);
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
        final BigInteger executeUnchecked(final BigInteger first, final BigInteger second) {
            return first.add(second);
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
        final BigInteger executeUnchecked(final BigInteger first, final BigInteger second) {
            return first.subtract(second);
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
        final BigInteger executeUnchecked(final BigInteger first, final BigInteger second) {
            return first.multiply(second);
        }
    }

    /**
     * This operator rounds towards zero like Java does. See
     * https://stackoverflow.com/questions/37795248/integer-division-in-java
     * 
     * @author martijn
     *
     */
    public static final class OperatorDivide extends BinaryOperator {
        public OperatorDivide(final int line, final int column) {
            super(line, column);
        }

        @Override
        public final String getName() {
            return "div";
        }

        @Override
        final BigInteger executeUnchecked(BigInteger first, BigInteger second) {
            if (second.equals(BigInteger.ZERO)) {
                throw new ProgramException(getLine(), getColumn(), "Division by zero");
            }
            return first.divide(second);
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
            return CountlangType.bool();
        }

        @Override
        public final int getNumArguments() {
            return 1;
        }

        @Override
        public final boolean checkAndEstablishTypes(List<CountlangType> argumentTypes) {
            return argumentTypes.get(0) == CountlangType.bool();
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
            return CountlangType.bool();
        }

        @Override
        public final int getNumArguments() {
            return 2;
        }

        @Override
        public final boolean checkAndEstablishTypes(List<CountlangType> argumentTypes) {
            return (argumentTypes.get(0) == CountlangType.bool())
                    && (argumentTypes.get(1) == CountlangType.bool());
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
            return (argumentTypes.get(0) == CountlangType.integer())
                    && (argumentTypes.get(1) == CountlangType.integer());
        }

        @Override
        public final CountlangType getResultType() {
            return CountlangType.bool();
        }

        @Override
        public final Object execute(List<Object> arguments) {
            BigInteger i1 = (BigInteger) arguments.get(0);
            BigInteger i2 = (BigInteger) arguments.get(1);
            return Boolean.valueOf(executeInt(i1, i2));
        }

        abstract boolean executeInt(final BigInteger i1, final BigInteger i2);
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
        final boolean executeInt(final BigInteger i1, final BigInteger i2) {
            return i1.compareTo(i2) < 0;
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
        final boolean executeInt(final BigInteger i1, final BigInteger i2) {
            return i1.compareTo(i2) <= 0;
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
        final boolean executeInt(final BigInteger i1, final BigInteger i2) {
            return i1.compareTo(i2) > 0;
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
        final boolean executeInt(final BigInteger i1, final BigInteger i2) {
            return i1.compareTo(i2) >= 0;
        }
    }

    public static abstract class MultiTypeRelOp extends Operator {
        CountlangType argType = CountlangType.unknown();

        MultiTypeRelOp(final int line, final int column) {
            super(line, column);
        }

        @Override
        public final int getNumArguments() {
            return 2;
        }

        @Override
        public final boolean checkAndEstablishTypes(List<CountlangType> argumentTypes) {
            boolean result = (argumentTypes.get(0) == argumentTypes.get(1)) && argumentTypes.get(0).isPrimitive();
            if(result) {
                argType = argumentTypes.get(0);
            }
            return result;
        }

        @Override
        public final CountlangType getResultType() {
            return CountlangType.bool();
        }

        @Override
        public final Object execute(List<Object> arguments) {
            boolean result = false; 
            if(argType == CountlangType.bool()) {
                boolean b1 = (Boolean) arguments.get(0);
                boolean b2 = (Boolean) arguments.get(1);
                result = applyBool(b1, b2);
            } else if(argType == CountlangType.integer()) {
                BigInteger i1 = (BigInteger) arguments.get(0);
                BigInteger i2 = (BigInteger) arguments.get(1);
                result = applyInt(i1, i2);
            } else {
                throw new IllegalStateException("Cannot execute when type checking failed");
            }
            return Boolean.valueOf(result);
        }        

        abstract boolean applyInt(final BigInteger i1, final BigInteger i2);
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
        final boolean applyInt(final BigInteger i1, final BigInteger i2) {
            return i1.equals(i2);
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
        final boolean applyInt(final BigInteger i1, final BigInteger i2) {
            return ! i1.equals(i2);
        }

        @Override
        final boolean applyBool(final boolean b1, final boolean b2) {
            return b1 != b2;
        }
    }

    public static class OperatorKnown extends Operator {
        private CountlangType resultType;

        public OperatorKnown(final int line, final int column) {
            super(line, column);
        }

        @Override
        public final String getName() {
            return "known";
        }

        @Override
        public final Object execute(final List<Object> arguments) {
            return ((Distribution) arguments.get(0)).getDistributionOfKnown();
        }

        @Override
        public final int getNumArguments() {
            return 1;
        }

        @Override
        public final boolean checkAndEstablishTypes(final List<CountlangType> argumentTypes) {
            resultType = argumentTypes.get(0);
            return argumentTypes.get(0).isDistribution();
        }

        @Override
        public final CountlangType getResultType() {
            return resultType;
        }
    }
}
