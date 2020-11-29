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

package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.ExpressionsAndStatementsCombinationHandler.State.DOING_EXPRESSIONS;
import static com.github.mhdirkse.countlang.steps.ExpressionsAndStatementsCombinationHandler.State.DOING_STATEMENTS;
import static com.github.mhdirkse.countlang.steps.ExpressionsAndStatementsCombinationHandler.State.DONE;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.IfStatement;

final class IfStatementCalculation extends ExpressionsAndStatementsCombinationHandler {
    private final IfStatement ifStatement;
    Boolean selectorValue = null;

    IfStatementCalculation(IfStatement ifStatement) {
        this.ifStatement = ifStatement;
    }

    private IfStatementCalculation(IfStatementCalculation orig) {
        super(orig);
        this.ifStatement = orig.ifStatement;
        selectorValue = new Boolean(orig.selectorValue);
    }

    @Override
    public AstNode getAstNode() {
        return ifStatement;
    }

    @Override
    AstNode stepBefore(ExecutionContext context) {
        setState(DOING_EXPRESSIONS);
        return ifStatement.getSelector();
    }

    @Override
    void acceptChildResultDoingExpressions(Object value, ExecutionContext context) {
        selectorValue = (Boolean) value;
    }

    @Override
    AstNode stepDoingExpressions(ExecutionContext context) {
        setState(DOING_STATEMENTS);
        if(selectorValue.booleanValue()) {
            return ifStatement.getThenStatement();
        } else if(ifStatement.getElseStatement() != null) {
            return ifStatement.getElseStatement();
        } else {
            return null;
        }
    }

    @Override
    boolean isAcceptingChildResultsDoingStatements() {
        return false;
    }

    @Override
    public void acceptChildResultDoingStatements(Object value, ExecutionContext context) {
        throw new IllegalStateException("Programming error: Cannot handle child result after getting selector expression");
    }

    @Override
    AstNode stepDoingStatements(ExecutionContext context) {
        setState(DONE);
        return null;
    }

    @Override
    public AstNodeExecution fork() {
        return new IfStatementCalculation(this);
    }
}
