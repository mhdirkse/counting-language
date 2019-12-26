package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.ast.Statement;

class PrintStatementHandler extends AbstractExpressionHandler implements StatementSource {
    private PrintStatement statement;

    @Override
    public Statement getStatement() {
        return statement;
    }

    PrintStatementHandler(final int line, final int column) {
        statement = new PrintStatement(line, column);
    }

    @Override
    void addExpression(final ExpressionNode expression) {
        statement.setExpression(expression);
    }
}
