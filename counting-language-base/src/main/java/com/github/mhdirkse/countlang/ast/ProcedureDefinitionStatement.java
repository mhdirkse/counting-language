package com.github.mhdirkse.countlang.ast;

public class ProcedureDefinitionStatement extends FunctionDefinitionStatementBase {
	public ProcedureDefinitionStatement(int line, int column) {
		super(line, column);
	}

	@Override
	public void accept(Visitor v) {
		v.visitProcedureDefinitionStatement(this);
	}
}
