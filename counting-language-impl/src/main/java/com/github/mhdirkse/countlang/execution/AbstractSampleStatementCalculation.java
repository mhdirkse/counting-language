/*
 * Copyright Martijn Dirkse 2022
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

package com.github.mhdirkse.countlang.execution;

import static com.github.mhdirkse.countlang.execution.AstNodeExecutionState.AFTER;
import static com.github.mhdirkse.countlang.execution.AstNodeExecutionState.RUNNING;

import java.math.BigInteger;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.algorithm.ProbabilityTreeValue;
import com.github.mhdirkse.countlang.algorithm.SampleContext;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CompositeNode;
import com.github.mhdirkse.countlang.ast.ProgramException;

abstract class AbstractSampleStatementCalculation implements AstNodeExecution {
    SubExpressionStepper delegate;
    final CompositeNode statement;
    SampleContext sampleContext;
    Distribution distribution;
    boolean isSamplingStarted = false;
    Object value;

    AbstractSampleStatementCalculation(CompositeNode statement, SampleContext sampleContext) {
        this.statement = statement;
        this.delegate = new SubExpressionStepper(statement.getSubExpressions());
        this.sampleContext = sampleContext;
    }

    @Override
    public AstNode getAstNode() {
        return (AstNode) statement;
    }

    @Override
    public final AstNode step(ExecutionContext context) {
        if(! delegate.isDone()) {
            return delegate.step(context);
        }
        if(distribution.getTotal().equals(BigInteger.ZERO)) {
            throw new ProgramException(((AstNode) statement).getLine(), ((AstNode) statement).getColumn(), "Cannot sample from empty distribution.");
        }
        if(! isSamplingStarted) {
            startSampledVariable();
            isSamplingStarted = true;
        }
        if(sampleContext.hasNextValue()) {
            ProbabilityTreeValue item = sampleContext.nextValue();
            if(item.isUnknown()) {
                sampleContext.scoreUnknown();
                return null;
            }
            value = item.getValue();
            context.forkExecutor();
            return null;
        }
        sampleContext.stopSampledVariable();
        // Removes the executor that was triggering this object's step method.
        // This object will be removed, even though the state never advances
        // to AFTER.
        context.stopExecutor();
        return null;
    }

    abstract void startSampledVariable();

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
}
