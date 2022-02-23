package com.github.mhdirkse.countlang.ast;

public class NonValueReturnStatement extends Statement {
	public NonValueReturnStatement(int line, int column) {
		super(line, column);
	}

	@Override
	public void accept(Visitor v) {
		v.visitNonValueReturnStatement(this);
	}
}
