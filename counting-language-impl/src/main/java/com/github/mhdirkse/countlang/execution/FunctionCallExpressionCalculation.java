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
import java.util.stream.Collectors;

import com.github.mhdirkse.countlang.algorithm.SampleContext;
import com.github.mhdirkse.countlang.algorithm.ScopeAccess;
import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.Call;
import com.github.mhdirkse.countlang.ast.ExperimentDefinitionStatement;
import com.github.mhdirkse.countlang.ast.FunctionDefinition;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatementBase;
import com.github.mhdirkse.countlang.ast.PredefinedFunction;
import com.github.mhdirkse.countlang.ast.ProcedureCallStatement;
import com.github.mhdirkse.countlang.ast.ProcedureDefinitionStatement;
import com.github.mhdirkse.countlang.ast.ProgramException;

final class FunctionCallExpressionCalculation extends ExpressionsAndStatementsCombinationHandler {
    private final Call call;
    private final SubExpressionStepper subExpressionStepper;
    private FunctionDefinitionStatementBase funWithStatement;
    private StatementsHandler statementsHandler;
    private boolean isStartedAfterFork = false;

    FunctionCallExpressionCalculation(final Call expression) {
        this.call = expression;
        this.subExpressionStepper = new SubExpressionStepper(expression.getSubExpressions());
    }

    private FunctionCallExpressionCalculation(final FunctionCallExpressionCalculation orig) {
        super(orig);
        this.call = orig.call;
        this.subExpressionStepper = new SubExpressionStepper(orig.subExpressionStepper);
        this.funWithStatement = orig.funWithStatement;
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
        return (AstNode) call;
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
        List<Object> subExpressionResults = subExpressionStepper.getSubExpressionResults();
        FunctionDefinition fun = context.getFunction(call.getKey());
        if(fun instanceof ExperimentDefinitionStatement) {
            statementsHandler = new StatementsHandlerExperiment(((ExperimentDefinitionStatement) fun).isPossibilityCounting());
        } else if(fun instanceof FunctionDefinitionStatement) {
            statementsHandler = new StatementsHandlerFunction();
        } else if(fun instanceof ProcedureDefinitionStatement) {
        	statementsHandler = new StatementsHandlerFunction();
        } else if(fun instanceof PredefinedFunction) {
            return runPredefinedFunction(context, subExpressionResults, (PredefinedFunction) fun);
        } else {
            throw new IllegalArgumentException("Unknown implementation of FunctionDefinition encountered");
        }
        setState(DOING_STATEMENTS);
        funWithStatement = (FunctionDefinitionStatementBase) fun;
        context.pushVariableFrame(ScopeAccess.HIDE_PARENT);
        for(int i = 0; i < funWithStatement.getFormalParameters().size(); i++) {
            String parameterName = funWithStatement.getFormalParameters().getFormalParameter(i).getName();
            Object value = subExpressionResults.get(i);
            context.writeSymbol(parameterName, value, funWithStatement.getFormalParameters().getFormalParameter(i));
        }
        statementsHandler.forkIfNeeded(context);
        return null;
    }

    private AstNode runPredefinedFunction(ExecutionContext context, List<Object> args, PredefinedFunction fun) {
        AstNode asNode = (AstNode) call;
    	Object result = fun.run(asNode.getLine(), asNode.getColumn(), args);
        if(result == null) {
            String argsStr = args.stream().map(Object::toString).collect(Collectors.joining(", "));
        	throw new IllegalStateException(String.format("Predefined function %s returned null on arguments %s", fun.getKey().toString(), argsStr));
        }
        context.onResult(result);
        setState(DONE);
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
            return funWithStatement.getSubStatements().get(0);
        }

        @Override
        void acceptChildResultDoingStatements(Object value, ExecutionContext context) {
            functionResult = value;
        }

        @Override
        void after(ExecutionContext context) {
            if(call instanceof ProcedureCallStatement) {
            	return;
            } else if(functionResult == null) {
                throw new ProgramException(getAstNode().getLine(), getAstNode().getColumn(), String.format("Function %s does not return a value", funWithStatement.getKey().toString()));
            } else {
            	context.onResult(functionResult);
            }
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
            return funWithStatement.getSubStatements().get(0);
        }

        @Override
        void acceptChildResultDoingStatements(Object value, ExecutionContext context) {
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
