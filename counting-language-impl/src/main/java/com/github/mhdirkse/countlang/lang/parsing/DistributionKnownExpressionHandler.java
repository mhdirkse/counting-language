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

package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.Operator;

class DistributionKnownExpressionHandler extends AbstractExpressionHandler implements ExpressionSource {
	private CompositeExpression expression;

	DistributionKnownExpressionHandler(int line, int column) {
		expression = new CompositeExpression(line, column);
		expression.setOperator(new Operator.OperatorKnown(line, column));
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
