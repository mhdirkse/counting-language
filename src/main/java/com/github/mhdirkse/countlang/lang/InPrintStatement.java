package com.github.mhdirkse.countlang.lang;

import com.github.mhdirkse.countlang.ast.Expression;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.ast.Statement;

final class InPrintStatement extends InStatement {
    private final PrintStatement printStatement;

    InPrintStatement(final int line, final int column) {
        printStatement = new PrintStatement(line, column);
    }

    @Override
    final void handleExpression(final Expression expression) {
        printStatement.setExpression(expression);
    }

    @Override
    final Statement getStatement() {
        return printStatement;
    }
}
