package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.EmptyCollectionExpression;
import com.github.mhdirkse.countlang.ast.ExpressionNode;

abstract class AbstractTypeHandler extends AbstractCountlangListenerHandler implements ExpressionSource {
    final int line;
    final int column;

    AbstractTypeHandler(int line, int column) {
        super(false);
        this.line = line;
        this.column = column;
    }

    int getLine() {
        return line;
    }

    int getColumn() {
        return column;
    }

    abstract CountlangType getCountlangType();

    @Override
    public ExpressionNode getExpression() {
        return new EmptyCollectionExpression(line, column, getCountlangType());
    }
}
