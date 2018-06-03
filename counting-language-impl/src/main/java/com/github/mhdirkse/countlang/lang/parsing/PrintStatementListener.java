package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.Expression;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class PrintStatementListener extends AbstractChildExpressionListener {
    private final PrintStatement statement;
    private final AbstractListener parent;

    PrintStatementListener(final int line, final int column, final AbstractListener parent) {
        this.statement = new PrintStatement(line, column); 
        this.parent = parent;
    }

    @Override
    public void handleChildExpression(final Expression expression) {
        statement.setExpression(expression);
    }

    @Override
    public void exitPrintStatementImpl(CountlangParser.PrintStatementContext ctx) {
        parent.visitPrintStatement(statement);
        delegate = null;
    }
}
