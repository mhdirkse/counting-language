package com.github.mhdirkse.countlang.ast;

public class ExperimentDefinitionStatement extends FunctionDefinitionStatementBase {
    public ExperimentDefinitionStatement(int line, int column) {
        super(line, column);
    }

    @Override
    public CountlangType getReturnType() {
        return CountlangType.DISTRIBUTION;
    }

    @Override
    public void accept(Visitor v) {
        v.visitExperimentDefinitionStatement(this);
    }
}
