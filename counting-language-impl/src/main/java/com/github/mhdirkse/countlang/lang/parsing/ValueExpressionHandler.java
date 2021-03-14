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
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.ProgramException;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.lang.CountlangLexer;

class ValueExpressionHandler extends AbstractCountlangListenerHandler implements ExpressionSource {
    private final int line;
    private final int column;
    private ValueExpression expression;

    @Override
    public ExpressionNode getExpression() {
        return expression;
    }

    ValueExpressionHandler(final int line, final int column) {
        super(false);
        this.line = line;
        this.column = column;
    }

    @Override
    public boolean visitTerminal(
            final TerminalNode node,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        if(node.getSymbol().getType() == CountlangLexer.BOOL) {
            expression = new ValueExpression(line, column, Boolean.valueOf(node.getText()), CountlangType.bool());
            return true;
        } else if(node.getSymbol().getType() == CountlangLexer.INT) {
            try {
                expression = new ValueExpression(line, column, Integer.valueOf(node.getText()), CountlangType.integer());
            } catch(NumberFormatException e) {
                throw new ProgramException(line, column, "Integer value is too big to store: " + node.getText());
            }
            return true;
        }
        return false;
    }
}
