package com.github.mhdirkse.countlang.lang.parsing;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.RangeExpression;

class RangeExpressionHandler extends AbstractExpressionHandler implements ExpressionSource {
	private int line;
	private int column;
	private List<ExpressionNode> subExpressions = new ArrayList<>();
	private ExpressionNode result = null;

	RangeExpressionHandler(int line, int column) {
		this.line = line;
		this.column = column;
	}

	@Override
	void addExpression(ExpressionNode expression) {
		subExpressions.add(expression);
	}

	@Override
	public ExpressionNode getExpression() {
		if(result == null) {
			result = new RangeExpression(line, column, subExpressions);
		}
		return result;
	}
}
