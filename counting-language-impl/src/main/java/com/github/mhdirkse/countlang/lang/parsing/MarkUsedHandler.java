package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.MarkUsedStatement;
import com.github.mhdirkse.countlang.ast.Statement;

class MarkUsedHandler extends AbstractExpressionHandler implements StatementSource {
    private MarkUsedStatement statement;

    @Override
    public Statement getStatement() {
        return statement;
    }

    MarkUsedHandler(final int line, final int column) {
        statement = new MarkUsedStatement(line, column);
    }

    @Override
    void addExpression(final ExpressionNode expression) {
        statement.setExpression(expression);
    }
}
