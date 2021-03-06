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

import static com.github.mhdirkse.countlang.execution.ExpressionsAndStatementsCombinationHandler.State.DOING_EXPRESSIONS;
import static com.github.mhdirkse.countlang.execution.ExpressionsAndStatementsCombinationHandler.State.DOING_STATEMENTS;
import static com.github.mhdirkse.countlang.execution.ExpressionsAndStatementsCombinationHandler.State.DONE;

import java.util.List;

import com.github.mhdirkse.countlang.algorithm.SampleContext;
import com.github.mhdirkse.countlang.algorithm.ScopeAccess;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.ExperimentDefinitionStatement;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatementBase;
import com.github.mhdirkse.countlang.ast.ProgramException;

final class FunctionCallExpressionCalculation extends ExpressionsAndStatementsCombinationHandler {
    private final FunctionCallExpression expression;
    private final SubExpressionStepper subExpressionStepper;
    private FunctionDefinitionStatementBase fun;
    private StatementsHandler statementsHandler;
    private boolean isStartedAfterFork = false;

    FunctionCallExpressionCalculation(final FunctionCallExpression expression) {
        this.expression = expression;
        this.subExpressionStepper = new SubExpressionStepper(expression.getSubExpressions());
    }

    private FunctionCallExpressionCalculation(final FunctionCallExpressionCalculation orig) {
        super(orig);
        this.expression = orig.expression;
        this.subExpressionStepper = new SubExpressionStepper(orig.subExpressionStepper);
        this.fun = orig.fun;
        this.isStartedAfterFork = orig.isStartedAfterFork;
        this.statementsHandler = this.createStatementsHandlerFrom(orig);
    }

    private StatementsHandler createStatementsHandlerFrom(FunctionCallExpressionCalculation orig) {
        if(orig.statementsHandler instanceof StatementsHandlerExperiment) {
            return new StatementsHandlerExperimentForked(((StatementsHandlerExperiment) orig.statementsHandler).sampleContext);
        } else if(orig.statementsHandler instanceof StatementsHandlerExperimentForked){
            return new StatementsHandlerExperimentForked(((StatementsHandlerExperimentForked) orig.statementsHandler).sampleContext);
        } else {
            throw new IllegalStateException("Programming error: unexpected type of StatementsHandler encountered");
        }
    }

    @Override
    public AstNode getAstNode() {
        return expression;
    }

    @Override
    AstNode stepBefore(ExecutionContext context) {
        setState(DOING_EXPRESSIONS);
        return subExpressionStepper.step(context);
    }

    @Override
    void acceptChildResultDoingExpressions(Object value, ExecutionContext context) {
        subExpressionStepper.acceptChildResult(value);
    }

    @Override
    AstNode stepDoingExpressions(ExecutionContext context) {
        if(!subExpressionStepper.isDone()) {
            return subExpressionStepper.step(context);
        }
        setState(DOING_STATEMENTS);
        fun = context.getFunction(expression.getFunctionName());
        if(fun instanceof ExperimentDefinitionStatement) {
            statementsHandler = new StatementsHandlerExperiment(((ExperimentDefinitionStatement) fun).isPossibilityCounting());
        } else {
            statementsHandler = new StatementsHandlerFunction();
        }
        context.pushVariableFrame(ScopeAccess.HIDE_PARENT);
        List<Object> subExpressionResults = subExpressionStepper.getSubExpressionResults();
        for(int i = 0; i < fun.getFormalParameters().size(); i++) {
            String parameterName = fun.getFormalParameters().getFormalParameter(i).getName();
            Object value = subExpressionResults.get(i);
            context.writeSymbol(parameterName, value, fun.getFormalParameters().getFormalParameter(i));
        }
        statementsHandler.forkIfNeeded(context);
        return null;
    }

    /**
     * Used by Stepper to give the {@link SampleContext} to {@link SampleStatementCalculation} instances.
     */
    SampleContext getSampleContext() {
        if(statementsHandler == null) {
            throw new IllegalStateException("There is no SampleContext before the statement of the called function is executed");
        }
        return statementsHandler.getSampleContext();
    }

    private abstract class StatementsHandler {
        abstract void forkIfNeeded(ExecutionContext context);
        abstract AstNode start();
        abstract void acceptChildResultDoingStatements(Object value, ExecutionContext context);
        abstract void after(ExecutionContext context);
        abstract SampleContext getSampleContext();
    }

    private class StatementsHandlerFunction extends StatementsHandler {
        private Object functionResult;

        StatementsHandlerFunction() {
        }

        void forkIfNeeded(ExecutionContext context) {
        }

        @Override
        AstNode start() {
            return fun.getSubStatements().get(0);
        }

        @Override
        void acceptChildResultDoingStatements(Object value, ExecutionContext context) {
            context.stopFunctionCall(expression);
            functionResult = value;
        }

        @Override
        void after(ExecutionContext context) {
            if(functionResult == null) {
                throw new ProgramException(getAstNode().getLine(), getAstNode().getColumn(), String.format("Function %s does not return a value", fun.getName()));
            }
            context.onResult(functionResult);
        }

        @Override
        SampleContext getSampleContext() {
            throw new IllegalStateException("StatementsHandlerFunction does not have a SampleContext");
        }
    }

    private class StatementsHandlerExperiment extends StatementsHandler {
        SampleContext sampleContext;

        StatementsHandlerExperiment(boolean isPossibilityCounting) {
            sampleContext = SampleContext.getInstance(isPossibilityCounting);
        }

        void forkIfNeeded(ExecutionContext context) {
            context.forkExecutor();
        }

        @Override
        AstNode start() {
            return null;
        }

        @Override
        void acceptChildResultDoingStatements(Object value, ExecutionContext context) {
            throw new IllegalStateException("Should not happen because only StatementsHandlerExperimentForked receives results");
        }

        @Override
        void after(ExecutionContext context) {
            context.onResult(sampleContext.getResult());
        }

        @Override
        SampleContext getSampleContext() {
            return sampleContext;
        }
    }

    private class StatementsHandlerExperimentForked
    extends StatementsHandler {
        private SampleContext sampleContext;

        StatementsHandlerExperimentForked(SampleContext sampleContext) {
            this.sampleContext = sampleContext;
        }

        void forkIfNeeded(ExecutionContext context) {
        }

        @Override
        AstNode start() {
            return fun.getSubStatements().get(0);
        }

        @Override
        void acceptChildResultDoingStatements(Object value, ExecutionContext context) {
            context.stopFunctionCall(expression);
            sampleContext.score(value);
        }

        @Override
        void after(ExecutionContext context) {
            if(! sampleContext.isScored()) {
                sampleContext.scoreUnknown();
            }
            context.stopExecutor();
        }

        @Override
        SampleContext getSampleContext() {
            return sampleContext;
        }
    }

    @Override
    boolean isAcceptingChildResultsDoingStatements() {
        return true;
    }

    @Override
    void acceptChildResultDoingStatements(Object value, ExecutionContext context) {
        statementsHandler.acceptChildResultDoingStatements(value, context);
    }
    
    @Override
    AstNode stepDoingStatements(ExecutionContext context) {
        if(! isStartedAfterFork) {
            isStartedAfterFork = true;
            return statementsHandler.start();
        }
        setState(DONE);
        context.popVariableFrame();
        statementsHandler.after(context);
        return null;
    }

    @Override
    public AstNodeExecution fork() {
        if((statementsHandler == null) || (statementsHandler instanceof StatementsHandlerFunction)) {
            throw new IllegalStateException("Calling a function is not expected on the call stack when running an experiment is started");
        }
        return new FunctionCallExpressionCalculation(this);
    }
}
