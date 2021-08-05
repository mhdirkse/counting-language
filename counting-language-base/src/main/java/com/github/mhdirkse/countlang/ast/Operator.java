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
import java.util.stream.Collectors;

import org.apache.commons.math3.fraction.BigFraction;

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
    	private CountlangType resultType;

        public OperatorUnaryMinus(final int line, final int column) {
    		super(line, column);
    	}

    	@Override
    	public final String getName() {
    		return "unaryMinus";
    	}

    	@Override
    	public final Object execute(final List<Object> arguments) {
    		Object arg = arguments.get(0);
    		if(arg instanceof BigInteger) {
    		    return ((BigInteger) arg).negate();    		    
    		} else if(arg instanceof BigFraction) {
    		    return ((BigFraction) arg).negate();
    		}
    	    throw new IllegalArgumentException("Argument type not supported");
    	}

    	@Override
    	public final int getNumArguments() {
    	    return 1;
    	}

    	@Override
    	public final boolean checkAndEstablishTypes(final List<CountlangType> argumentTypes) {
    	    resultType = argumentTypes.get(0);
    	    return argumentTypes.get(0).isPrimitiveNumeric();
    	}

    	@Override
    	public final CountlangType getResultType() {
    	    return resultType;
    	}
    }

    private static abstract class BinaryOperator extends Operator {
        CountlangType resultType;
        CountlangType inputType;
    	boolean mustCastFirstToFrac = false;
    	boolean mustCastSecondToFrac = false;

        BinaryOperator(final int line, final int column) {
    		super(line, column);
    	}

    	@Override
    	public final Object execute(final List<Object> arguments) {
    	    if(inputType == CountlangType.bool()) {
    	        return executeBoolUnchecked((Boolean) arguments.get(0), (Boolean) arguments.get(1));
    	    } else if(inputType == CountlangType.integer()) {
    	        return executeIntUnchecked((BigInteger) arguments.get(0), (BigInteger) arguments.get(1));
    	    } else if(inputType == CountlangType.fraction()) {
    	        BigFraction firstArg = null;
    	        BigFraction secondArg = null;
    	        if(mustCastFirstToFrac) {
    	            firstArg = new BigFraction((BigInteger) arguments.get(0));
    	        } else {
    	            firstArg = (BigFraction) arguments.get(0);
    	        }
    	        if(mustCastSecondToFrac) {
    	            secondArg = new BigFraction((BigInteger) arguments.get(1));
    	        } else {
    	            secondArg = (BigFraction) arguments.get(1);
    	        }
    	        return executeFracUnchecked(firstArg, secondArg);
    	    }
    	    throw new IllegalArgumentException(String.format("Type mismatch executing operator %s with arguments %s",
    	            getName(), arguments.stream().map(Object::toString).collect(Collectors.joining(", "))));
    	}

    	@Override
    	public final int getNumArguments() {
    	    return 2;
    	}

    	@Override
    	public final boolean checkAndEstablishTypes(final List<CountlangType> argumentTypes) {
    	    if(argumentTypes.stream().allMatch(t -> t.equals(CountlangType.bool()))) {
    	        inputType = CountlangType.bool();
    	    } else if(argumentTypes.stream().allMatch(CountlangType::isPrimitiveNumeric)) {
    	        if(argumentTypes.stream().allMatch(t -> t.equals(CountlangType.integer()))) {
    	            inputType = CountlangType.integer();
    	        } else {
    	            inputType = CountlangType.fraction();
    	            if(argumentTypes.get(0).equals(CountlangType.integer())) {
    	                mustCastFirstToFrac = true;
    	            }
    	            if(argumentTypes.get(1).equals(CountlangType.integer())) {
    	                mustCastSecondToFrac = true;
    	            }
    	        }
    	    } else {
    	        return false;
    	    }
    	    return checkAndEstablishTypes();
    	}

    	@Override
    	public final CountlangType getResultType() {
    	    return resultType;
    	}

    	abstract boolean checkAndEstablishTypes();
    	abstract Object executeBoolUnchecked(Boolean firstArg, Boolean secondArg);
    	abstract Object executeIntUnchecked(BigInteger firstArg, BigInteger secondArg);
    	abstract Object executeFracUnchecked(BigFraction firstArg, BigFraction secondArg);
    }

    private static abstract class BinaryNumericOperator extends BinaryOperator {
        BinaryNumericOperator(int line, int column) {
            super(line, column);
        }

        @Override
        final boolean checkAndEstablishTypes() {
            if(inputType == CountlangType.bool()) {
                return false;
            }
            resultType = inputType;
            return true;
        }

        final Object executeBoolUnchecked(Boolean firstArg, Boolean secondArg) {
            throw new IllegalArgumentException(String.format("Operator %s does not support Boolean arguments", getName()));
        }

        @Override
        abstract BigInteger executeIntUnchecked(BigInteger firstArg, BigInteger secondArg);

        @Override
        abstract BigFraction executeFracUnchecked(BigFraction firstArg, BigFraction secondArg);
    }

    public static final class OperatorAdd extends BinaryNumericOperator {
        public OperatorAdd(final int line, final int column) {
            super(line, column);
        }

        @Override
        public final String getName() {
            return "+";
        }

        @Override
        final BigInteger executeIntUnchecked(final BigInteger first, final BigInteger second) {
            return first.add(second);
        }

        @Override
        final BigFraction executeFracUnchecked(final BigFraction first, final BigFraction second) {
            return first.add(second);
        }
    }

    public static final class OperatorSubtract extends BinaryNumericOperator {
        public OperatorSubtract(final int line, final int column) {
            super(line, column);
        }

        @Override
        public final String getName() {
            return "-";
        }

        @Override
        final BigInteger executeIntUnchecked(final BigInteger first, final BigInteger second) {
            return first.subtract(second);
        }

        @Override
        final BigFraction executeFracUnchecked(final BigFraction first, final BigFraction second) {
            return first.subtract(second);
        }
    }

    public static final class OperatorMultiply extends BinaryNumericOperator {
        public OperatorMultiply(final int line, final int column) {
            super(line, column);
        }

        @Override
        public final String getName() {
            return "*";
        }

        @Override
        final BigInteger executeIntUnchecked(final BigInteger first, final BigInteger second) {
            return first.multiply(second);
        }

        @Override
        final BigFraction executeFracUnchecked(final BigFraction first, final BigFraction second) {
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
        boolean checkAndEstablishTypes() {
            if(inputType == CountlangType.integer()) {
                resultType = CountlangType.integer();
                return true;
            }
            return false;
        }

        @Override
        Object executeBoolUnchecked(Boolean firstArg, Boolean secondArg) {
            throw new IllegalArgumentException("Input type Boolean not supported");
        }

        @Override
        Object executeIntUnchecked(BigInteger firstArg, BigInteger secondArg) {
            if (secondArg.equals(BigInteger.ZERO)) {
                throw new ProgramException(getLine(), getColumn(), "Division by zero");
            }
            return firstArg.divide(secondArg);
        }

        @Override
        Object executeFracUnchecked(BigFraction firstArg, BigFraction secondArg) {
            throw new IllegalArgumentException("Input type Fraction not supported");
        }
    }

    public static class OperatorFrac extends BinaryOperator {
        public OperatorFrac(int line, int column) {
            super(line, column);
        }

        @Override
        boolean checkAndEstablishTypes() {
            if(inputType == CountlangType.bool()) {
                return false;
            }
            resultType = CountlangType.fraction();
            return true;
        }

        @Override
        Object executeBoolUnchecked(Boolean firstArg, Boolean secondArg) {
            throw new IllegalArgumentException("Input type Boolean not supported");
        }

        @Override
        BigFraction executeIntUnchecked(BigInteger firstArg, BigInteger secondArg) {
            if(secondArg.compareTo(BigInteger.ZERO) == 0) {
                throw new ProgramException(getLine(), getColumn(), "Division by zero");
            }
            return new BigFraction(firstArg, secondArg);
        }

        @Override
        BigFraction executeFracUnchecked(BigFraction firstArg, BigFraction secondArg) {
            if(secondArg.compareTo(BigFraction.ZERO) == 0) {
                throw new ProgramException(getLine(), getColumn(), "Division by zero");
            }
            return firstArg.divide(secondArg);
        }

        @Override
        public String getName() {
            return "/";
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

    public static abstract class BinBoolOperator extends BinaryOperator {
        BinBoolOperator(final int line, final int column) {
            super(line, column);
        }

        @Override
        final boolean checkAndEstablishTypes() {
            if(inputType == CountlangType.bool()) {
                resultType = CountlangType.bool();
                return true;
            }
            return false;
        }

        @Override
        abstract Boolean executeBoolUnchecked(Boolean arg0, Boolean arg1);

        @Override
        final Boolean executeIntUnchecked(BigInteger arg0, BigInteger arg1) {
            throw new IllegalArgumentException("Input type integer not supported");
        }
        
        @Override
        final Boolean executeFracUnchecked(BigFraction arg0, BigFraction arg1) {
            throw new IllegalArgumentException("Input type fraction not supported");
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
        public final Boolean executeBoolUnchecked(Boolean arg0, Boolean arg1) {
            return Boolean.logicalAnd(arg0, arg1);
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
        public final Boolean executeBoolUnchecked(Boolean arg0, Boolean arg1) {
            return Boolean.logicalOr(arg0, arg1);
        }
    }

    public static abstract class RelOp extends BinaryOperator {
        RelOp(final int line, final int column) {
            super(line, column);
        }

        @Override
        final boolean checkAndEstablishTypes() {
            if(inputType == CountlangType.bool()) {
                return false;
            }
            if(mustCastFirstToFrac || mustCastSecondToFrac) {
                return false;
            }
            resultType = CountlangType.bool();
            return true;
        }

        final Boolean executeBoolUnchecked(final Boolean arg0, final Boolean arg1) {
            throw new IllegalArgumentException("Input type Boolean not supported");
        }

        @Override
        abstract Boolean executeIntUnchecked(final BigInteger i1, final BigInteger i2);

        @Override
        abstract Boolean executeFracUnchecked(final BigFraction f1, final BigFraction f2);
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
        final Boolean executeIntUnchecked(final BigInteger i1, final BigInteger i2) {
            return i1.compareTo(i2) < 0;
        }

        @Override
        final Boolean executeFracUnchecked(final BigFraction f1, final BigFraction f2) {
            return f1.compareTo(f2) < 0;
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
        final Boolean executeIntUnchecked(final BigInteger i1, final BigInteger i2) {
            return i1.compareTo(i2) <= 0;
        }

        @Override
        final Boolean executeFracUnchecked(final BigFraction f1, final BigFraction f2) {
            return f1.compareTo(f2) <= 0;
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
        final Boolean executeIntUnchecked(final BigInteger i1, final BigInteger i2) {
            return i1.compareTo(i2) > 0;
        }

        @Override
        final Boolean executeFracUnchecked(final BigFraction f1, final BigFraction f2) {
            return f1.compareTo(f2) > 0;
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
        final Boolean executeIntUnchecked(final BigInteger i1, final BigInteger i2) {
            return i1.compareTo(i2) >= 0;
        }

        @Override
        final Boolean executeFracUnchecked(final BigFraction f1, final BigFraction f2) {
            return f1.compareTo(f2) >= 0;
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
            boolean result = (argumentTypes.get(0) == argumentTypes.get(1));
            if(result) {
                argType = argumentTypes.get(0);
            }
            return result;
        }

        @Override
        public final CountlangType getResultType() {
            return CountlangType.bool();
        }
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
        public Object execute(List<Object> args) {
            return args.get(0).equals(args.get(1));
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
        public Object execute(List<Object> args) {
            return ! args.get(0).equals(args.get(1));
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
