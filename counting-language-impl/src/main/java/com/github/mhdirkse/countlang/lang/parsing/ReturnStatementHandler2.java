package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.ReturnStatement;
import com.github.mhdirkse.countlang.ast.Statement;

class ReturnStatementHandler2 extends AbstractExpressionHandler2 implements StatementSource {
    private ReturnStatement statement;

    @Override
    public Statement getStatement() {
        return statement;
    }

    ReturnStatementHandler2(final int line, final int column) {
        statement = new ReturnStatement(line, column);
    }

    @Override
    public void addExpression(final ExpressionNode expression) {
        statement.setExpression(expression);
    }
}
