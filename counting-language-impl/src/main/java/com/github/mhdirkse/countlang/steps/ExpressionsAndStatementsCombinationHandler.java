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

import com.github.mhdirkse.countlang.ast.AstNode;

abstract class ExpressionsAndStatementsCombinationHandler implements AstNodeExecution {
    enum State {
        BEFORE(AstNodeExecutionState.BEFORE),
        DOING_EXPRESSIONS(AstNodeExecutionState.RUNNING),
        DOING_STATEMENTS(AstNodeExecutionState.RUNNING),
        DONE(AstNodeExecutionState.AFTER);

        private final AstNodeExecutionState generalState;

        State(AstNodeExecutionState generalState) {
            this.generalState = generalState;
        }

        AstNodeExecutionState getGeneralState() {
            return generalState;
        }
    }

    private State state = State.BEFORE;

    ExpressionsAndStatementsCombinationHandler() {
    }

    ExpressionsAndStatementsCombinationHandler(ExpressionsAndStatementsCombinationHandler orig) {
        this.state = orig.state;
    }

    @Override
    public final AstNodeExecutionState getState() {
        return state.getGeneralState();
    }

    final void setState(final State state) {
        this.state = state;
    }

    @Override
    public final AstNode step(ExecutionContext context) {
        switch(state) {
        case BEFORE:
            return stepBefore(context);
        case DOING_EXPRESSIONS:
            return stepDoingExpressions(context);
        case DOING_STATEMENTS:
            return stepDoingStatements(context);
        default:
            return null;
        }
    }

    @Override
    public final boolean isAcceptingChildResults() {
        switch(state) {
        case DOING_EXPRESSIONS:
            return true;
        case DOING_STATEMENTS:
            return isAcceptingChildResultsDoingStatements();
        default:
            return false;
        }
    }

    @Override
    public final void acceptChildResult(Object value, ExecutionContext context) {
        switch(state) {
        case DOING_EXPRESSIONS:
            acceptChildResultDoingExpressions(value, context);
            break;
        case DOING_STATEMENTS:
            acceptChildResultDoingStatements(value, context);
            break;
        default:
            throw new IllegalStateException("Programming error: Unexpected child result");
        }
    }

    abstract AstNode stepBefore(ExecutionContext context);
    abstract AstNode stepDoingExpressions(ExecutionContext context);
    abstract AstNode stepDoingStatements(ExecutionContext context);
    abstract boolean isAcceptingChildResultsDoingStatements();
    abstract void acceptChildResultDoingExpressions(Object value, ExecutionContext context);
    abstract void acceptChildResultDoingStatements(Object value, ExecutionContext context);
}
