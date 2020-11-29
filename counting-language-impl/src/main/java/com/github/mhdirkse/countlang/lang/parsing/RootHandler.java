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

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class RootHandler extends AbstractCountlangListenerHandler {
    private StatementGroup statementGroup = null;

    RootHandler() {
        super(false);
    }

    StatementGroup getProgram() {
        return statementGroup;
    }

    @Override
    public boolean enterProg(
            @NotNull CountlangParser.ProgContext antlrCtx, final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return true;
    }

    @Override
    public boolean exitProg(
            @NotNull CountlangParser.ProgContext antlrCtx, final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return true;
    }

    @Override
    public boolean enterStatements(
            @NotNull CountlangParser.StatementsContext ctx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegationCtx.addFirst(new StatementGroupHandler(line, column));
        return true;
    }

    @Override
    public boolean exitStatements(
            @NotNull CountlangParser.StatementsContext ctx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        statementGroup = ((StatementGroupHandler) delegationCtx.getPreviousHandler()).getStatementGroup();
        delegationCtx.removeAllPreceeding();
        return true;        
    }
}
