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
    	private CountlangType argType = CountlangType.integer();

    	public OperatorUnaryMinus(final int line, final int column) {
    		super(line, column);
    	}

    	@Override
    	public final String getName() {
    		return "unaryMinus";
    	}

    	@Override
    	public final Object execute(final List<Object> arguments) {
    		if(argType == CountlangType.integer()) {
    			BigInteger arg = (BigInteger) arguments.get(0);
    			return executeInt(arg);
    		} else if(argType == CountlangType.fraction()) {
    			BigFraction arg = (BigFraction) arguments.get(0);
    			return executeFrac(arg);
    		} else {
    			throw new IllegalArgumentException(String.format("Type is not supported: ", argType.toString()));
    		}
    	}

    	private final Object executeInt(BigInteger arg) {
    		return arg.negate();
    	}

    	private final Object executeFrac(BigFraction arg) {
    		return arg.negate();
    	}

    	@Override
    	public final int getNumArguments() {
    	    return 1;
    	}

    	@Override
    	public final boolean checkAndEstablishTypes(final List<CountlangType> argumentTypes) {
    	    argType = argumentTypes.get(0);
    	    if((argType == CountlangType.integer()) || (argType == CountlangType.fraction())) {
    	    	return true;
    	    } else {
    	    	return false;
    	    }
    	}

    	@Override
    	public final CountlangType getResultType() {
    	    return argType;
    	}
    }

    /**
     * This operator rounds towards zero like Java does. See
     * https://stackoverflow.com/questions/37795248/integer-division-in-java
     * 
     * @author martijn
     *
     */
    public static final class OperatorDivide extends Operator {
        public OperatorDivide(final int line, final int column) {
            super(line, column);
        }

        @Override
        public final String getName() {
            return "div";
        }

		@Override
		public Object execute(List<Object> arguments) {
            BigInteger first = (BigInteger) arguments.get(0);
            BigInteger second = (BigInteger) arguments.get(1);
			if (second.equals(BigInteger.ZERO)) {
                throw new ProgramException(getLine(), getColumn(), "Division by zero");
            }
            return first.divide(second);
		}

		@Override
		public int getNumArguments() {
			return 2;
		}

		@Override
		public boolean checkAndEstablishTypes(List<CountlangType> argumentTypes) {
			return (argumentTypes.get(0) == CountlangType.integer()) && (argumentTypes.get(1) == CountlangType.integer());
		}

		@Override
		public CountlangType getResultType() {
			return CountlangType.integer();
		}
    }

    private static abstract class BinaryOperator extends Operator {
    	CountlangType resultType = null;

    	BinaryOperator(final int line, final int column) {
    		super(line, column);
    	}

    	@Override
    	public Object execute(final List<Object> arguments) {
    		Object arg0 = arguments.get(0);
    		Object arg1 = arguments.get(1);
    		if(arg0 instanceof BigInteger) {
    			if(arg1 instanceof BigInteger) {
    	    		BigInteger firstArg = (BigInteger) arg0;
    	            BigInteger secondArg = (BigInteger) arg1;
    	            return executeIntUnchecked(firstArg, secondArg);    				
    			} else if(arg1 instanceof BigFraction) {
    				BigFraction firstArg = new BigFraction((BigInteger) arg0);
    				BigFraction secondArg = (BigFraction) arg1;
    				return executeFracUnchecked(firstArg, secondArg);
    			} else {
    				throw new IllegalArgumentException("Unsupported type for second argument: " + arg1.getClass().getName());
    			}
    		} else if(arg0 instanceof BigFraction) {
    			if(arg1 instanceof BigInteger) {
    				BigFraction firstArg = (BigFraction) arg0;
    				BigFraction secondArg = new BigFraction((BigInteger) arg1);
    				return executeFracUnchecked(firstArg, secondArg);
    			} else if(arg1 instanceof BigFraction) {
    				return executeFracUnchecked((BigFraction) arg0, (BigFraction) arg1);
    			} else {
    				throw new IllegalArgumentException("Unsupported type for second argument: " + arg1.getClass().getName());
    			}
    		} else {
    			throw new IllegalArgumentException("Unsupported type for first argument: " + arg0.getClass().getName());
    		}
    	}

    	@Override
    	public final int getNumArguments() {
    	    return 2;
    	}

    	@Override
    	public boolean checkAndEstablishTypes(final List<CountlangType> argumentTypes) {
    	    boolean result = argumentTypes.stream().allMatch(CountlangType::isPrimitiveNumeric);
    	    if(! result) {
    	    	return false;
    	    }
    	    boolean receivesFrac = argumentTypes.stream().anyMatch(t -> t.equals(CountlangType.fraction()));
    	    setResultType(receivesFrac);
    	    return true;
    	}

    	@Override
    	public final CountlangType getResultType() {
    	    return resultType;
    	}

    	abstract void setResultType(boolean receivesFrac);
        abstract Object executeIntUnchecked(BigInteger firstArg, BigInteger secondArg);
        abstract Object executeFracUnchecked(BigFraction firstArg, BigFraction secondArg);
    }

    private static abstract class BinaryNumericOperator extends BinaryOperator {
    	BinaryNumericOperator(final int line, final int column) {
    		super(line, column);
    	}

    	@Override
    	void setResultType(boolean receivesFrac) {
    	    resultType = CountlangType.integer();
    	    if(receivesFrac) {
    	    	resultType = CountlangType.fraction();
    	    }
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

    public static abstract class RelOp extends BinaryOperator {
        RelOp(final int line, final int column) {
            super(line, column);
        }

        @Override
        public final void setResultType(boolean receivesFrac) {
            resultType = CountlangType.bool();
        }

        @Override
        abstract Boolean executeIntUnchecked(BigInteger firstArg, BigInteger secondArg);

        @Override
        abstract Boolean executeFracUnchecked(BigFraction firstFrac, BigFraction secondFrac);
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
        final Boolean executeFracUnchecked(final BigFraction i1, final BigFraction i2) {
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
        final Boolean executeIntUnchecked(final BigInteger i1, final BigInteger i2) {
            return i1.compareTo(i2) <= 0;
        }

        @Override
        final Boolean executeFracUnchecked(final BigFraction i1, final BigFraction i2) {
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
        final Boolean executeIntUnchecked(final BigInteger i1, final BigInteger i2) {
            return i1.compareTo(i2) > 0;
        }

        @Override
        final Boolean executeFracUnchecked(final BigFraction i1, final BigFraction i2) {
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
        final Boolean executeIntUnchecked(final BigInteger i1, final BigInteger i2) {
            return i1.compareTo(i2) >= 0;
        }

        @Override
        final Boolean executeFracUnchecked(final BigFraction i1, final BigFraction i2) {
            return i1.compareTo(i2) >= 0;
        }
    }

    public static abstract class MultiTypeRelOp extends BinaryOperator {
        private CountlangType inputType = null;

    	MultiTypeRelOp(final int line, final int column) {
            super(line, column);
        }

        @Override
        public final boolean checkAndEstablishTypes(List<CountlangType> argumentTypes) {
            boolean result = super.checkAndEstablishTypes(argumentTypes);
            if(! result) {
            	boolean isBool = argumentTypes.stream().allMatch(t -> t.equals(CountlangType.bool()));
            	if(isBool) {
            		inputType = CountlangType.bool();
            		resultType = CountlangType.bool();
            		result = true;
            	}
            }
            return result;
        }

        @Override
        void setResultType(boolean receivesFrac) {
        	if(receivesFrac) {
        		inputType = CountlangType.fraction();
        	} else {
        		inputType = CountlangType.integer();
        	}
        	resultType = CountlangType.bool();
        }

        @Override
        public final Object execute(List<Object> arguments) {
            boolean result = false;
            if(inputType == CountlangType.bool()) {
                boolean b1 = (Boolean) arguments.get(0);
                boolean b2 = (Boolean) arguments.get(1);
                result = executeBoolUnchecked(b1, b2);
            } else if(inputType == CountlangType.integer()) {
                BigInteger i1 = (BigInteger) arguments.get(0);
                BigInteger i2 = (BigInteger) arguments.get(1);
                result = executeIntUnchecked(i1, i2);
            } else if(inputType == CountlangType.fraction()) {
            	BigFraction f1 = null;
            	BigFraction f2 = null;
            	if(arguments.get(0) instanceof BigInteger) {
            		f1 = new BigFraction((BigInteger) arguments.get(0));
            	} else {
            		f1 = (BigFraction) arguments.get(0);
            	}
            	if(arguments.get(1) instanceof BigInteger) {
            		f2 = new BigFraction((BigInteger) arguments.get(1));
            	} else {
            		f2 = (BigFraction) arguments.get(1);
            	}
            	result = executeFracUnchecked(f1, f2);
            } else {
                throw new IllegalStateException("Cannot execute when type checking failed");
            }
            return Boolean.valueOf(result);
        }        

        @Override
        abstract Boolean executeFracUnchecked(final BigFraction f1, final BigFraction f2);

        @Override
        abstract Boolean executeIntUnchecked(final BigInteger i1, final BigInteger i2);
        
        abstract boolean executeBoolUnchecked(final boolean b1, final boolean b2);
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
        final Boolean executeFracUnchecked(final BigFraction f1, final BigFraction f2) {
        	return f1.equals(f2);
        }

        @Override
        final Boolean executeIntUnchecked(final BigInteger i1, final BigInteger i2) {
            return i1.equals(i2);
        }

        @Override
        final boolean executeBoolUnchecked(final boolean b1, final boolean b2) {
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
        final Boolean executeFracUnchecked(BigFraction f1, final BigFraction f2) {
        	return ! f1.equals(f2);
        }

        @Override
        final Boolean executeIntUnchecked(final BigInteger i1, final BigInteger i2) {
            return ! i1.equals(i2);
        }

        @Override
        final boolean executeBoolUnchecked(final boolean b1, final boolean b2) {
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
