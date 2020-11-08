package com.github.mhdirkse.countlang.ast;

import lombok.Getter;
import lombok.Setter;

public class FunctionDefinitionStatement extends FunctionDefinitionStatementBase {
    @Getter
    @Setter
    private CountlangType returnType = CountlangType.UNKNOWN;

    public FunctionDefinitionStatement(final int line, final int column) {
        super(line, column);
    }


    @Override
    public void accept(final Visitor v) {
        v.visitFunctionDefinitionStatement(this);
    }
}
