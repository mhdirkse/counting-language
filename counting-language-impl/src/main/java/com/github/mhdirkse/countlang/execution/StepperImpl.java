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

package com.github.mhdirkse.countlang.execution;

import com.github.mhdirkse.countlang.algorithm.Distribution;
import com.github.mhdirkse.countlang.algorithm.ProbabilityTreeValue;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.utils.Stack;

class StepperImpl implements Stepper, StepperCallback {
    private final Stack<Executor> executors = new Stack<>();

    StepperImpl(final AstNode target, final ExecutionContext context, final AstNodeExecutionFactory factory) {
        executors.push(new Executor(context, factory, factory.create(target, null)));
    }

    @Override
    public boolean hasMoreSteps() {
        if(executors.isEmpty()) {
            return false;
        }
        return executors.peek().hasMoreSteps();
    }

    @Override
    public void step() {
        if(!hasMoreSteps()) {
            throw new IllegalStateException("No more steps");
        }
        executors.peek().step();
    }

    @Override
    public Object onResult(Object value) {
        return executors.peek().onResult(value);
    }

    @Override
    public void stopFunctionCall(FunctionCallExpression functionCallExpression) {
        executors.peek().stopFunctionCall(functionCallExpression);
    }

    @Override
    public ExecutionPoint getExecutionPoint() {
        return executors.peek().getExecutionPoint();
    }

    @Override
    public void forkExecutor() {
        executors.push(new Executor(executors.peek()));
    }

    @Override
    public void stopExecutor() {
        executors.pop();
    }

    @Override
    public void startSampledVariable(int line, int column, Distribution sampledDistribution) {
        executors.peek().startSampledVariable(line, column, sampledDistribution);
    }

    @Override
    public void stopSampledVariable() {
        executors.peek().stopSampledVariable();
    }

    @Override
    public boolean hasNextValue() {
        return executors.peek().hasNextValue();
    }

    @Override
    public ProbabilityTreeValue nextValue() {
        return executors.peek().nextValue();
    }

    @Override
    public void scoreUnknown() {
        executors.peek().scoreUnknown();
    }
}
