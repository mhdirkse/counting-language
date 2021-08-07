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

import java.util.List;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.ast.TypeNode;
import com.github.mhdirkse.countlang.lang.CountlangParser;

abstract class FunctionDefinitionStatementHandlerBase extends AbstractCountlangListenerHandler
implements TerminalFilterCallback {
    private final TerminalFilter terminalFilter;

    FunctionDefinitionStatementHandlerBase(int line, int column) {
        super(false);
        terminalFilter = new TerminalFilter(this);
    }

    @Override
    public boolean enterStatements(
            @NotNull CountlangParser.StatementsContext ctx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegationCtx.addFirst(new StatementGroupHandler(line, column));
        return true;   
    }

    @Override
    public boolean exitStatements(
            @NotNull CountlangParser.StatementsContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        if(delegationCtx.isFirst()) {
            return false;
        } else {
            StatementGroup statements = ((StatementGroupHandler) delegationCtx.getPreviousHandler()).getStatementGroup();
            addStatementGroup(statements);
            delegationCtx.removeAllPreceeding();
            return true;
        }
    }

    abstract void addStatementGroup(StatementGroup statementGroup);

    @Override
    public boolean visitTerminal(
            @NotNull TerminalNode node,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return terminalFilter.visitTerminal(node, delegationCtx);
    }

    @Override
    public int getRequiredType() {
        return CountlangParser.ID;
    }

    @Override
    public boolean enterVarDecls(
            @NotNull CountlangParser.VarDeclsContext antlrCtx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        delegationCtx.addFirst(new VarDeclsHandler());
        return true;
    }

    @Override
    public boolean exitVarDecls(
            @NotNull CountlangParser.VarDeclsContext antlrCtx,
            final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        if (delegationCtx.isFirst()) {
            return false;
        } else {
            List<FormalParameter> formalParameters = ((VarDeclsHandler) delegationCtx.getPreviousHandler()).getFormalParameters();
            for (FormalParameter formalParameter : formalParameters) {
                addFormalParameter(formalParameter.getName(), formalParameter.getTypeNode());
            }
            delegationCtx.removeAllPreceeding();
            return true;
        }
    }

    abstract void addFormalParameter(String name, TypeNode typeNode);
}
