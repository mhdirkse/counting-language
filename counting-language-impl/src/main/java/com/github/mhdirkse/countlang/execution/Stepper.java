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

import java.util.List;

import com.github.mhdirkse.countlang.algorithm.OutputStrategy;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.FunctionDefinition;
import com.github.mhdirkse.countlang.ast.FunctionDefinitions;

public interface Stepper {
    boolean hasMoreSteps();
    void step();
    ExecutionPoint getExecutionPoint();
    
    default void run() {
        while(hasMoreSteps() ) {
            step();
        }
    }

    default void runUntil(ExecutionPoint executionPoint) {
        while(hasMoreSteps() && ! getExecutionPoint().equals(executionPoint)) {
            step();
        }
    }

    public static Stepper getInstance(
            final AstNode target, final OutputStrategy outputStrategy, List<FunctionDefinition> predefinedFunctions) {
        ExecutionScopeStack executionScopeStack = new ExecutionScopeStack();
        FunctionDefinitions funDefs = new FunctionDefinitions();
        predefinedFunctions.forEach(f -> funDefs.putFunction(f));
        AstNodeExecutionFactory factory = new AstNodeExecutionFactoryCalculate();
        ExecutionContextCalculate context = new ExecutionContextCalculate(executionScopeStack, funDefs, outputStrategy);
        StepperImpl result = new StepperImpl(target, context, factory);
        context.setStepperCallback(result);
        return result;
    }
}
