package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.ExpressionsAndStatementsCombinationHandler.State.DOING_EXPRESSIONS;
import static com.github.mhdirkse.countlang.steps.ExpressionsAndStatementsCombinationHandler.State.DOING_STATEMENTS;
import static com.github.mhdirkse.countlang.steps.ExpressionsAndStatementsCombinationHandler.State.DONE;

import java.util.List;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;
import com.github.mhdirkse.countlang.execution.StackFrameAccess;

final class FunctionCallExpressionCalculation extends ExpressionsAndStatementsCombinationHandler {
    private final FunctionCallExpression expression;
    private final SubExpressionStepper subExpressionStepper;
    private Object functionResult = null;

    FunctionCallExpressionCalculation(final FunctionCallExpression expression) {
        this.expression = expression;
        this.subExpressionStepper = new SubExpressionStepper(expression.getSubExpressions());
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
        // TODO: Implement executiong experiments
        FunctionDefinitionStatement fun = (FunctionDefinitionStatement) context.getFunction(expression.getFunctionName());
        context.pushVariableFrame(StackFrameAccess.HIDE_PARENT);
        List<Object> subExpressionResults = subExpressionStepper.getSubExpressionResults();
        for(int i = 0; i < fun.getFormalParameters().size(); i++) {
            String parameterName = fun.getFormalParameters().getFormalParameter(i).getName();
            Object value = subExpressionResults.get(i);
            context.writeSymbol(parameterName, value, fun.getFormalParameters().getFormalParameter(i));
        }
        setState(DOING_STATEMENTS);
        return fun.getSubStatements().get(0);
    }

    @Override
    boolean isAcceptingChildResultsDoingStatements() {
        return true;
    }

    @Override
    void acceptChildResultDoingStatements(Object value, ExecutionContext context) {
        context.stopFunctionCall(expression);
        functionResult = value;
    }
    
    @Override
    AstNode stepDoingStatements(ExecutionContext context) {
        context.onResult(functionResult);
        setState(DONE);
        context.popVariableFrame();
        return null;
    }
}
