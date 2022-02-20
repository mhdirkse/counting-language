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
