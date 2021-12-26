package com.github.mhdirkse.countlang.ast;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.type.CountlangType;

public class RangeExpression extends ExpressionNode implements CompositeNode {
	private CountlangType countlangType = CountlangType.unknown();
	private ExpressionNode start;
	private ExpressionNode endInclusive;
	private ExpressionNode step;

	public RangeExpression(int line, int column, List<ExpressionNode> subExpressions) {
		super(line, column);
		if(subExpressions.size() == 2) {
			start = subExpressions.get(0);
			endInclusive = subExpressions.get(1);
		} else if(subExpressions.size() == 3) {
			start = subExpressions.get(0);
			step = subExpressions.get(1);
			endInclusive = subExpressions.get(2);
		} else {
			throw new IllegalArgumentException(String.format("RangExpression constructor has invalid number of arguments: %d", subExpressions.size()));
		}
	}

	public boolean hasExplicitStep() {
		return step != null;
	}

	public ExpressionNode getStart() {
		return start;
	}

	public boolean isConstant() {
		boolean result = (start instanceof ValueExpression);
		result = result && (endInclusive instanceof ValueExpression);
		if(hasExplicitStep()) {
			result = result && (step instanceof ValueExpression);
		}
		return result;
	}

	/**
	 * If this is an integer range defined by constant expressions, return the components.
	 */
	public List<BigInteger> getIntegerComponents() {
		if(! isConstant()) {
			throw new IllegalStateException("Range is not constant");
		}
		if(! (countlangType == CountlangType.rangeOf(CountlangType.integer()))) {
			throw new IllegalStateException("Range is not integer");
		}
		List<BigInteger> result = new ArrayList<>();
		result.add(intValueOf(start));
		result.add(intValueOf(endInclusive));
		if(hasExplicitStep()) {
			result.add(intValueOf(step));
		}
		return result;
	}

	private BigInteger intValueOf(ExpressionNode childNode) {
		Object value = ((ValueExpression) childNode).getValue();
		return (BigInteger) value;
	}

	public ExpressionNode getEndInclusive() {
		return endInclusive;
	}

	public ExpressionNode getStep() {
		return step;
	}

	@Override
	public List<AstNode> getChildren() {
		List<AstNode> result = new ArrayList<>();
		result.add(start);
		result.add(endInclusive);
		if(hasExplicitStep()) {
			result.add(step);
		}
		return result;
	}

	@Override
	public CountlangType getCountlangType() {
		return countlangType;
	}

	public void setCountlangType(CountlangType countlangType) {
		this.countlangType = countlangType;
	}

	@Override
	public void accept(Visitor v) {
		v.visitRangeExpression(this);
	}
}
