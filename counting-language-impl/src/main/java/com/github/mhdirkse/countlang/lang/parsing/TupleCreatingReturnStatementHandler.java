/*
 * Copyright Martijn Dirkse 2022
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

import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.ValueReturnStatement;
import com.github.mhdirkse.countlang.ast.Statement;
import com.github.mhdirkse.countlang.ast.TupleExpression;

class TupleCreatingReturnStatementHandler extends AbstractExpressionHandler implements StatementSource {
	private ValueReturnStatement statement;

	TupleCreatingReturnStatementHandler(int line, int column) {
		statement = new ValueReturnStatement(line, column);
		statement.setExpression(new TupleExpression(line, column));
	}

    @Override
    public Statement getStatement() {
        return statement;
    }

    @Override
    public void addExpression(final ExpressionNode expression) {
    	((TupleExpression) statement.getExpression()).addChild(expression);
    }
}
