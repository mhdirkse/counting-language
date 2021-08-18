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
import com.github.mhdirkse.countlang.ast.TupleDealingLhs;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class TupleDealingSampleStatementHandler extends AbstractExpressionHandler
implements StatementSource {
    private LeftHandSideStrategy lhsStrategy = new LeftHandSideStrategy();
    private SampleStatement statement;

    @Override
    public Statement getStatement() {
        return statement;
    }

    TupleDealingSampleStatementHandler(final int line, final int column) {
        statement = new SampleStatement(line, column);
    }

    @Override
    public boolean enterLhsItem(CountlangParser.LhsItemContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return lhsStrategy.enterLhsItem(antlrCtx, delegationCtx);
    }

    @Override
    public boolean exitLhsItem(CountlangParser.LhsItemContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return lhsStrategy.exitLhsItem(antlrCtx, delegationCtx);
    }

    @Override
    public boolean visitTerminal(
            final TerminalNode node, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        if(lhsStrategy.isInLhsItem()) {
            return lhsStrategy.visitTerminal(node, delegationCtx);
        } else if(node.getText().equals("from")) {
            TupleDealingLhs lhs = new TupleDealingLhs(statement.getLine(), statement.getColumn());
            lhs.setChildren(lhsStrategy.getLhsItems());
            statement.setLhs(lhs);
            return true;
        } else {
            throw new IllegalStateException(String.format("Unexpected token with text %s", node.getText()));
        }
    }

    @Override
    public void addExpression(final ExpressionNode expression) {
        statement.setSampledDistribution(expression);
    }
}
