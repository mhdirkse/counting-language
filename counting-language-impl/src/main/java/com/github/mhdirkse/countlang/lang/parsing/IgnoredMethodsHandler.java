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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class IgnoredMethodsHandler extends AbstractCountlangListenerHandler {
    private final Set<Integer> RELEVANT_TOKENS = new HashSet<>(
            Arrays.asList(CountlangParser.ID, CountlangParser.INT, CountlangParser.BOOL));

    IgnoredMethodsHandler() {
        super(false);
    }

    @Override
    public boolean enterEveryRule(
            final ParserRuleContext antlrCtx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return true;
    }

    @Override
    public boolean exitEveryRule(
            final ParserRuleContext antlrCtx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return true;
    }

    @Override
    public boolean enterBracketExpression(CountlangParser.BracketExpressionContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return true;
    }

    @Override
    public boolean exitBracketExpression(
            final @NotNull CountlangParser.BracketExpressionContext antlrCtx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return true;
    }

    @Override
    public boolean visitTerminal(
            final @NotNull TerminalNode node,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        if (RELEVANT_TOKENS.contains(node.getSymbol().getType())) {
            return false;
        } else {
            return true;
        }
    }
}
