/*
 * Copyright Martijn Dirkse 2022
 *
 * This file is part of counting-language.
 *
 * counting-language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
