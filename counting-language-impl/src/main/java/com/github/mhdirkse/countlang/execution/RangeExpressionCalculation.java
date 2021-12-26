package com.github.mhdirkse.countlang.execution;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.fraction.BigFraction;

import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.RangeExpression;
import com.github.mhdirkse.countlang.type.CountlangType;
import com.github.mhdirkse.countlang.type.FractionRange;
import com.github.mhdirkse.countlang.type.IntegerRange;

class RangeExpressionCalculation extends ExpressionResultsCollector {
	RangeExpressionCalculation(RangeExpression node) {
		super(node);
	}

	@Override
	void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext context) {
		CountlangType countlangType = ((ExpressionNode) getAstNode()).getCountlangType();
		if(countlangType == CountlangType.rangeOf(CountlangType.integer())) {
			context.onResult(new IntegerRange(subExpressionResults.stream().map(o -> (BigInteger) o).collect(Collectors.toList())));
		} else if(countlangType == CountlangType.rangeOf(CountlangType.fraction())) {
			context.onResult(new FractionRange(subExpressionResults.stream().map(o -> (BigFraction) o).collect(Collectors.toList())));
		} else {
			throw new IllegalArgumentException("Cannot build range value of type " + countlangType.toString());
		}
	}
}
