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

package com.github.mhdirkse.countlang.tasks;

import java.util.List;

import com.github.mhdirkse.countlang.ast.CompositeExpression;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithTotal;
import com.github.mhdirkse.countlang.ast.DistributionExpressionWithUnknown;
import com.github.mhdirkse.countlang.ast.ExperimentDefinitionStatement;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatementBase;
import com.github.mhdirkse.countlang.ast.ProgramException;
import com.github.mhdirkse.countlang.ast.SampleStatement;
import com.github.mhdirkse.countlang.ast.SimpleDistributionExpression;
import com.github.mhdirkse.countlang.ast.SymbolExpression;
import com.github.mhdirkse.countlang.ast.ValueExpression;
import com.github.mhdirkse.countlang.execution.DummyValue;
import com.github.mhdirkse.countlang.execution.FunctionAndReturnCheck;
import com.github.mhdirkse.countlang.execution.FunctionAndReturnCheck.SimpleContext;
import com.github.mhdirkse.countlang.execution.FunctionAndReturnCheckSimple;
import com.github.mhdirkse.countlang.execution.SymbolFrameStackVariableUsage;
import com.github.mhdirkse.countlang.execution.VariableUsageEventHandler;
import com.github.mhdirkse.countlang.utils.Stack;

class VariableCheck extends AbstractCountlangAnalysis<DummyValue> implements VariableUsageEventHandler {
    private final SymbolFrameStackVariableUsage symbols;

    public static VariableCheck getInstance(
            final StatusReporter reporter,
            List<FunctionDefinitionStatement> predefinedFuns) {
        SymbolFrameStackVariableUsage symbols = new SymbolFrameStackVariableUsage();
        FunctionAndReturnCheck<DummyValue> functionAndReturnCheck =
                new FunctionAndReturnCheckSimple<>(SimpleContext<DummyValue>::new);
        VariableCheck instance = new VariableCheck(symbols, reporter, functionAndReturnCheck, predefinedFuns);
        return instance;
    }

    private VariableCheck(
            final SymbolFrameStackVariableUsage stackFrame,
            final StatusReporter reporter,
            final FunctionAndReturnCheck<DummyValue> functionAndReturnCheck,
            List<FunctionDefinitionStatement> predefinedFuns) {
        super(stackFrame, new Stack<DummyValue>(), reporter, functionAndReturnCheck, predefinedFuns);
        this.symbols = stackFrame;
    }

    @Override
    void onFunctionRedefined(FunctionDefinitionStatementBase previous, FunctionDefinitionStatementBase current) {
        throw new ProgramException(
                current.getLine(),
                current.getColumn(),
                "Should have been caught during type check");
    }

    @Override
    DummyValue getPseudoActualParameter(FormalParameter formalParameter) {
        return DummyValue.getInstance();
    }

    @Override
    void beforeFunctionLeft(FunctionDefinitionStatement fun, int line, int column) {
    }

    @Override
    void beforeExperimentLeft(ExperimentDefinitionStatement fun, int line, int column) {
    }

    @Override
    void checkSelectValue(final DummyValue value, final ExpressionNode selector) {       
    }

    @Override
    DummyValue getSampleResultValue() {
        return DummyValue.getInstance();
    }

    @Override
    DummyValue doCompositeExpression(
            final List<DummyValue> arguments,
            final CompositeExpression compositeExpression) {
        return DummyValue.getInstance();
    }

    @Override
    DummyValue checkFunctionCall(
            final List<DummyValue> arguments,
            final FunctionCallExpression expr,
            final FunctionDefinitionStatementBase fun) {
        return DummyValue.getInstance();
    }

    @Override
    DummyValue onUndefinedFunctionCalled(FunctionCallExpression functionCallExpression) {
        return DummyValue.getInstance();
    }

    @Override
    DummyValue representValue(ValueExpression valueExpression) {
        return DummyValue.getInstance();
    }

    @Override
    void onSymbolExpression(DummyValue value, SymbolExpression symbolExpression) {
    }

    public void listEvents() {
        symbols.listEvents(this);
    }

    @Override
    public void variableNotUsed(String name, int line, int column) {
        reporter.report(
                StatusCode.VAR_NOT_USED,
                line,
                column,
                name);
    }

    @Override
    void onNestedFunction(FunctionDefinitionStatementBase statement) {
        throw new IllegalArgumentException("Should have been caught by the type check");
    }

    @Override
    void afterReturn(int line, int column) {       
    }

    @Override
    void onStatement(int line, int column) {
    }

    @Override
    DummyValue doSimpleDistributionExpression(
            List<DummyValue> arguments, SimpleDistributionExpression expression) {
        return DummyValue.getInstance();
    }

    @Override
    DummyValue doDistributionExpressionWithTotal(
            List<DummyValue> arguments, DistributionExpressionWithTotal expression) {
        return DummyValue.getInstance();
    }

    @Override
    DummyValue doDistributionExpressionWithUnknown(
            List<DummyValue> arguments, DistributionExpressionWithUnknown expression) {
        return DummyValue.getInstance();
    }

    @Override
    void onSamplingOutsideExperiment(SampleStatement statement) {
    }

    @Override
    void checkSampledDistribution(DummyValue value, SampleStatement statement) {
    }
}
