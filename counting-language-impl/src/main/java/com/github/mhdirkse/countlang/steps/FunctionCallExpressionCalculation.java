package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.ExpressionsAndStatementsCombinationHandler.State.DOING_EXPRESSIONS;
import static com.github.mhdirkse.countlang.steps.ExpressionsAndStatementsCombinationHandler.State.DOING_STATEMENTS;
import static com.github.mhdirkse.countlang.steps.ExpressionsAndStatementsCombinationHandler.State.DONE;

import java.util.List;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.ExperimentDefinitionStatement;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatementBase;
import com.github.mhdirkse.countlang.execution.SampleContext;
import com.github.mhdirkse.countlang.execution.StackFrameAccess;

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
        this.statementsHandler = new StatementsHandlerExperimentForked(
                (StatementsHandlerExperiment) orig.statementsHandler);
        this.isStartedAfterFork = orig.isStartedAfterFork;
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
        fun = context.getFunction(expression.getFunctionName());
        if(fun instanceof ExperimentDefinitionStatement) {
            statementsHandler = new StatementsHandlerExperiment();
        } else {
            statementsHandler = new StatementsHandlerFunction();
        }
        context.pushVariableFrame(StackFrameAccess.HIDE_PARENT);
        List<Object> subExpressionResults = subExpressionStepper.getSubExpressionResults();
        for(int i = 0; i < fun.getFormalParameters().size(); i++) {
            String parameterName = fun.getFormalParameters().getFormalParameter(i).getName();
            Object value = subExpressionResults.get(i);
            context.writeSymbol(parameterName, value, fun.getFormalParameters().getFormalParameter(i));
        }
        setState(DOING_STATEMENTS);
        statementsHandler.forkIfNeeded(context);
        return null;
    }

    private abstract class StatementsHandler {
        abstract void forkIfNeeded(ExecutionContext context);
        abstract AstNode start();
        abstract void acceptChildResultDoingStatements(Object value, ExecutionContext context);
        abstract void after(ExecutionContext context);
    }

    private class StatementsHandlerFunction extends StatementsHandler {
        private Object functionResult;

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
            context.onResult(functionResult);
        }
    }

    private class StatementsHandlerExperiment extends StatementsHandler {
        SampleContext sampleContext = SampleContext.getInstance();

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
    }

    private class StatementsHandlerExperimentForked extends StatementsHandler {
        private SampleContext sampleContext;

        StatementsHandlerExperimentForked(StatementsHandlerExperiment orig) {
            this.sampleContext = orig.sampleContext;
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
            sampleContext.score(((Integer) value).intValue());
        }

        @Override
        void after(ExecutionContext context) {
            if(! sampleContext.isScored()) {
                sampleContext.scoreUnknown();
            }
            context.stopExecutor();
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
