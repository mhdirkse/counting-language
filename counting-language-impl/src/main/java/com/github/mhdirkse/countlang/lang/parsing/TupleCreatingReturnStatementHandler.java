package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.ReturnStatement;
import com.github.mhdirkse.countlang.ast.Statement;
import com.github.mhdirkse.countlang.ast.TupleExpression;

class TupleCreatingReturnStatementHandler extends AbstractExpressionHandler implements StatementSource {
	private ReturnStatement statement;

	TupleCreatingReturnStatementHandler(int line, int column) {
		statement = new ReturnStatement(line, column);
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
