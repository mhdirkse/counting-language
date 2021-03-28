package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.CountlangType;

abstract class AbstractTypeHandler extends AbstractCountlangListenerHandler {
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
}
