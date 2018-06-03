package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.Expression;
import com.github.mhdirkse.countlang.ast.ReturnStatement;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class ReturnStatementListener extends AbstractChildExpressionListener {
    private final ReturnStatement statement;
    private final AbstractListener parent;

    ReturnStatementListener(final int line, final int column, final AbstractListener parent) {
        this.statement = new ReturnStatement(line, column); 
        this.parent = parent;
    }

    @Override
    public void handleChildExpression(final Expression expression) {
        statement.setExpression(expression);
    }

    @Override
    public void exitReturnStatementImpl(CountlangParser.ReturnStatementContext ctx) {
        parent.visitReturnStatement(statement);
        delegate = null;
    }
}
