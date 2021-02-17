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
import com.github.mhdirkse.countlang.ast.Statement;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class StatementGroupHandler extends AbstractCountlangListenerHandler {
    private StatementGroup statementGroup;

    StatementGroup getStatementGroup() {
        return statementGroup;
    }

    StatementGroupHandler(final int line, final int column) {
        super(false);
        statementGroup = new StatementGroup(line, column);
    }

    @Override
    public boolean enterPrintStatement(
            @NotNull CountlangParser.PrintStatementContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new PrintStatementHandler(line, column));
        return true;
    }

    @Override
    public boolean enterMarkUsedStatement(
            @NotNull CountlangParser.MarkUsedStatementContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new MarkUsedHandler(line, column));
        return true;
    }

    @Override
    public boolean enterAssignmentStatement(
            @NotNull CountlangParser.AssignmentStatementContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new AssignmentStatementHandler(line, column));
        return true;
    }

    @Override
    public boolean enterSampleStatement(
            @NotNull CountlangParser.SampleStatementContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new SampleStatementHandler(line, column));
        return true;
    }

    @Override
    public boolean enterReturnStatement(
            @NotNull CountlangParser.ReturnStatementContext ctx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegationCtx.addFirst(new ReturnStatementHandler(line, column));
        return true;
    }

    @Override
    public boolean enterIfStatement(
            @NotNull CountlangParser.IfStatementContext ctx, 
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegationCtx.addFirst(new IfStatementHandler(line, column));
        return true;
    }

    @Override
    public boolean enterWhileStatement(
            @NotNull CountlangParser.WhileStatementContext ctx, 
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegationCtx.addFirst(new WhileStatementHandler(line, column));
        return true;
    }

    @Override
    public boolean enterFunctionDefinitionStatement(
            CountlangParser.FunctionDefinitionStatementContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new FunctionDefinitionStatementHandler(line, column));
        return true;
    }

    @Override
    public boolean enterExperimentDefinitionStatement(
            CountlangParser.ExperimentDefinitionStatementContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new ExperimentDefinitionStatementHandler(line, column));
        return true;
    }

    @Override
    public boolean enterCompoundStatement(
            CountlangParser.CompoundStatementContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new CompoundStatementHandler(line, column));
        return true;
    }

    @Override
    public boolean exitPrintStatement(
            @NotNull CountlangParser.PrintStatementContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleStatementExit(delegationCtx);
    }

    private boolean handleStatementExit(HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        if (delegationCtx.isFirst()) {
            return false;
        } else {
            Statement statement = ((StatementSource) delegationCtx.getPreviousHandler()).getStatement();
            getStatementGroup().addStatement(statement);
            delegationCtx.removeAllPreceeding();
            return true;
        }
    }

    @Override
    public boolean exitMarkUsedStatement(
            @NotNull CountlangParser.MarkUsedStatementContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleStatementExit(delegationCtx);
    }
    
    @Override
    public boolean exitAssignmentStatement(
            @NotNull CountlangParser.AssignmentStatementContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleStatementExit(delegationCtx);
    }

    @Override
    public boolean exitSampleStatement(
            @NotNull CountlangParser.SampleStatementContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleStatementExit(delegationCtx);
    }

    @Override
    public boolean exitReturnStatement(
            @NotNull CountlangParser.ReturnStatementContext ctx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleStatementExit(delegationCtx);
    }

    @Override
    public boolean exitIfStatement(
            @NotNull CountlangParser.IfStatementContext ctx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleStatementExit(delegationCtx);
    }

    @Override
    public boolean exitWhileStatement(
            @NotNull CountlangParser.WhileStatementContext ctx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleStatementExit(delegationCtx);
    }

    @Override
    public boolean exitFunctionDefinitionStatement(
            @NotNull CountlangParser.FunctionDefinitionStatementContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleStatementExit(delegationCtx);
    }

    @Override
    public boolean exitExperimentDefinitionStatement(
            @NotNull CountlangParser.ExperimentDefinitionStatementContext antlrCtx, HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleStatementExit(delegationCtx);
    }

    @Override
    public boolean exitCompoundStatement(
            CountlangParser.CompoundStatementContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        return handleStatementExit(delegationCtx);
    }
}
