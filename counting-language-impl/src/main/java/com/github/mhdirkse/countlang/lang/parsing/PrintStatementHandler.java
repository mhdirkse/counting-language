/*
 * Copyright Martijn Dirkse 2020
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

package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.PrintStatement;
import com.github.mhdirkse.countlang.ast.Statement;
import com.github.mhdirkse.countlang.format.Format;

class PrintStatementHandler extends AbstractExpressionHandler implements StatementSource {
    private PrintStatement statement;

    @Override
    public Statement getStatement() {
        return statement;
    }

    PrintStatementHandler(final int line, final int column) {
        statement = new PrintStatement(line, column);
    }

    @Override
    void addExpression(final ExpressionNode expression) {
        statement.setExpression(expression);
    }

    @Override
    public boolean visitTerminal (final org.antlr.v4.runtime.tree.TerminalNode terminalNode, final com.github.mhdirkse.codegen.runtime.HandlerStackContext<com.github.mhdirkse.countlang.lang.parsing.CountlangListenerHandler> p2) {
    	String text = terminalNode.getText();
    	if(text.equals("approx")) {
    		statement.setFormat(Format.APPROXIMATE);
    		return true;
    	} else if(text.equals("exact")) {
    		statement.setFormat(Format.EXACT);
    		return true;
    	} else {
    		return super.visitTerminal(terminalNode, p2);
    	}
    }
}
