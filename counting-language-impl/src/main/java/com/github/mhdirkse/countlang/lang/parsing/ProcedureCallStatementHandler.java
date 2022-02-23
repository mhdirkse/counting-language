package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.ProcedureCallStatement;
import com.github.mhdirkse.countlang.ast.Statement;

class ProcedureCallStatementHandler extends CallHandler implements StatementSource {
	ProcedureCallStatementHandler(ProcedureCallStatement statement) {
		super(statement);
	}

	@Override
	public Statement getStatement() {
		return (Statement) call;
	}
}
