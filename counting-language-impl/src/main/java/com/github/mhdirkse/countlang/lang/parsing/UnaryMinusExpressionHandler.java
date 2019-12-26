package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.Operator;

class UnaryMinusExpressionHandler extends AbstractExpressionHandler2 implements ExpressionSource {
	private CompositeExpression expression;

	UnaryMinusExpressionHandler(int line, int column) {
		expression = new CompositeExpression(line, column);
		expression.setOperator(new Operator.OperatorUnaryMinus(line, column));
	}

	@Override
	public ExpressionNode getExpression() {
		return expression;
	}

	@Override
	public boolean visitTerminal(
			TerminalNode node,
			HandlerStackContext<CountlangListenerHandler> delegationCtx) {
		return delegationCtx.isFirst();
	}

	@Override
	void addExpression(ExpressionNode childExpression) {
		expression.addSubExpression(childExpression);
	}
}
