package com.github.mhdirkse.countlang.lang;

import com.github.mhdirkse.countlang.ast.Expression;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.ast.Statement;

class InPrintStatement extends InStatement {
    private final PrintStatement printStatement;

    InPrintStatement() {
        printStatement = new PrintStatement();
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
