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

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.SampleStatement;
import com.github.mhdirkse.countlang.ast.Statement;
import com.github.mhdirkse.countlang.lang.CountlangLexer;

class SampleStatementHandler extends AbstractExpressionHandler
implements StatementSource, TerminalFilterCallback {
    private final TerminalFilter terminalFilter;
    private SampleStatement statement;

    @Override
    public Statement getStatement() {
        return statement;
    }

    SampleStatementHandler(final int line, final int column) {
        statement = new SampleStatement(line, column);
        terminalFilter = new TerminalFilter(this);
    }

    @Override
    public boolean visitTerminal(
            final TerminalNode node, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return terminalFilter.visitTerminal(node, delegationCtx);
    }

    @Override
    public int getRequiredType() {
        return CountlangLexer.ID;
    }

    @Override
    public void setText(final String text) {
        statement.setSymbol(text);
    }

    @Override
    public void addExpression(final ExpressionNode expression) {
        statement.setSampledDistribution(expression);
    }
}
