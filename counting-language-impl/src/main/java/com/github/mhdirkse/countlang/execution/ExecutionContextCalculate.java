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

import com.github.mhdirkse.countlang.algorithm.OutputStrategy;
import com.github.mhdirkse.countlang.algorithm.ScopeAccess;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinition;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatementBase;
import com.github.mhdirkse.countlang.ast.FunctionDefinitions;
import com.github.mhdirkse.countlang.ast.FunctionKey;

class ExecutionContextCalculate implements ExecutionContext {
    private final ExecutionScopeStack symbolFrame;
    private StepperCallback stepperCallback;
    private final FunctionDefinitions funDefs;
    private final OutputStrategy outputStrategy;

    ExecutionContextCalculate(
            final ExecutionScopeStack symbolFrame,
            final FunctionDefinitions funDefs,
            final OutputStrategy outputStrategy) {
        this.symbolFrame = symbolFrame;
        this.funDefs = funDefs;
        this.outputStrategy = outputStrategy;
    }

    ExecutionContextCalculate(ExecutionContextCalculate orig) {
        this.symbolFrame = new ExecutionScopeStack(orig.symbolFrame);
        this.stepperCallback = orig.stepperCallback;
        this.funDefs = orig.funDefs;
        this.outputStrategy = orig.outputStrategy;
    }

    void setStepperCallback(final StepperCallback stepperCallback) {
        this.stepperCallback = stepperCallback;
    }

    @Override
    public Object onResult(Object value) {
        return stepperCallback.onResult(value);
    }

    @Override
    public void stopFunctionCall(FunctionCallExpression expression) {
        stepperCallback.stopFunctionCall(expression);
    }

    @Override
    public Object readSymbol(String symbol, AstNode node) {
        return symbolFrame.read(symbol, node.getLine(), node.getColumn());
    }

    @Override
    public void writeSymbol(String symbol, Object value, AstNode node) {
        symbolFrame.write(symbol, value, node.getLine(), node.getColumn());
        
    }

    @Override
    public void pushVariableFrame(final ScopeAccess access) {
        symbolFrame.push(access);
    }

    @Override
    public void popVariableFrame() {
        symbolFrame.pop();
    }

    @Override
    public boolean hasFunction(FunctionKey key) {
        return funDefs.hasFunction(key);
    }

    @Override
    public void defineFunction(FunctionDefinitionStatementBase functionDefinitionStatement) {
        funDefs.putFunction(functionDefinitionStatement);
    }

    @Override
    public FunctionDefinition getFunction(FunctionKey key) {
        return funDefs.getFunction(key);
    }

    @Override
    public void output(String text) {
        outputStrategy.output(text);
    }

    @Override
    public void forkExecutor() {
        stepperCallback.forkExecutor();
    }

    @Override
    public void stopExecutor() {
        stepperCallback.stopExecutor();
    }
}
