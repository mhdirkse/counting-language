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

import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.AFTER;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.RUNNING;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.ProgramException;
import com.github.mhdirkse.countlang.ast.SampleStatement;

class SampleStatementCalculation implements AstNodeExecution {
    SubExpressionStepper delegate;
    final SampleStatement statement;
    Distribution distribution;
    boolean isSamplingStarted = false;
    int value;

    SampleStatementCalculation(SampleStatement statement) {
        this.statement = statement;
        this.delegate = new SubExpressionStepper(statement.getSubExpressions());
    }

    @Override
    public AstNode getAstNode() {
        return statement;
    }

    @Override
    public final AstNode step(ExecutionContext context) {
        if(distribution == null) {
            return delegate.step(context);
        }
        if(distribution.getTotal() == 0) {
            throw new ProgramException(statement.getLine(), statement.getColumn(), "Cannot sample from empty distribution.");
        }
        if(! isSamplingStarted) {
            context.startSampledVariable(distribution);
            isSamplingStarted = true;
        }
        if(context.hasNextValue()) {
            value = context.nextValue();
            context.forkExecutor();
            return null;
        }
        context.stopSampledVariable();
        context.stopExecutor();
        return null;
    }

    @Override
    public AstNodeExecutionState getState() {
        AstNodeExecutionState delegateState = delegate.getState();
        if(delegateState == AFTER) {
            return RUNNING;
        } else {
            return delegateState;
        }
    }

    @Override
    public boolean isAcceptingChildResults() {
        return true;
    }

    @Override
    public void acceptChildResult(Object value, ExecutionContext context) {
        distribution = (Distribution) value;
    }

    @Override
    public AstNodeExecution fork() {
        return new SampleStatementCalculationForked(this);
    }
}
