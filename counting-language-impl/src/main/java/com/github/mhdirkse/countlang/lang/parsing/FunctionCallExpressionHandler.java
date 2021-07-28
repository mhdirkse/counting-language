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

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionKey;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class FunctionCallExpressionHandler extends AbstractExpressionHandler
implements ExpressionSource, TerminalFilterCallback {
    private final TerminalFilter terminalFilter;
    private FunctionCallExpression expression;

    @Override
    public ExpressionNode getExpression() {
        return expression;
    }

    FunctionCallExpressionHandler(final int line, final int column) {
        expression = new FunctionCallExpression(line, column);
        terminalFilter = new TerminalFilter(this);
    }

    @Override
    public void addExpression(final ExpressionNode childExpression) {
        expression.addArgument(childExpression);
    }

    @Override
    public boolean visitTerminal(
            @NotNull final TerminalNode node,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return terminalFilter.visitTerminal(node, delegationCtx);
    }

    @Override
    public int getRequiredType() {
        return CountlangParser.ID;
    }

    @Override
    public void setText(final String text) {
        expression.setKey(new FunctionKey(text));
    }
}
