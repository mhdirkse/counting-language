package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.Expression;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.ast.Statement;

class PrintStatementHandler2 extends AbstractExpressionHandler2 implements StatementSource {
    private PrintStatement statement;

    @Override
    public Statement getStatement() {
        return statement;
    }

    PrintStatementHandler2(final int line, final int column) {
        statement = new PrintStatement(line, column);
    }

    @Override
    void addExpression(final Expression expression) {
        statement.setExpression(expression);
    }
}
