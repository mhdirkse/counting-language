package com.github.mhdirkse.countlang.analysis;

import com.github.mhdirkse.countlang.ast.Statement;

interface RootOrFunctionCodeBlock {
	/**
	 * Checks that the right kind of return statement is being used. Functions return a value while
	 * procedures do not return a value.
	 *
	 * We cannot just take the return value as the argument here, because that complicates unit testing.
	 *
	 * @param line
	 * @param column
	 * @param returnStatementType
	 */
	void checkReturnStatementType(int line, int column, Class<? extends Statement> returnStatementType);
}
