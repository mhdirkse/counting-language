package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.ExpressionsAndStatementsCombinationHandler.State.DOING_EXPRESSIONS;
import static com.github.mhdirkse.countlang.steps.ExpressionsAndStatementsCombinationHandler.State.DOING_STATEMENTS;
import static com.github.mhdirkse.countlang.steps.ExpressionsAndStatementsCombinationHandler.State.DONE;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;

class FunctionCallExpressionCalculation extends ExpressionsAndStatementsCombinationHandler.Calculation {
    private final FunctionCallExpression expression;
    private final SubExpressionStepper<Object> subExpressionStepper;
    private final List<Object> subExpressionResults = new ArrayList<>();
    private Object functionResult = null;

    FunctionCallExpressionCalculation(final FunctionCallExpression expression) {
        this.expression = expression;
        this.subExpressionStepper = new SubExpressionStepper<Object>(expression.getSubExpressions());
    }

    @Override
    public AstNode getAstNode() {
        return expression;
    }

    @Override
    AstNode stepBefore(ExecutionContext<Object> context) {
        setState(DOING_EXPRESSIONS);
        return subExpressionStepper.step(context);
    }

    @Override
    boolean handleDescendantResultDoingExpressions(Object value) {
        subExpressionResults.add(value);
        return true;
    }

    @Override
    AstNode stepDoingExpressions(ExecutionContext<Object> context) {
        if(!subExpressionStepper.isDone()) {
            return subExpressionStepper.step(context);
        }
        FunctionDefinitionStatement fun = context.getFunction(expression.getFunctionName());
        context.pushVariableFrame();
        for(int i = 0; i < fun.getFormalParameters().size(); i++) {
            String parameterName = fun.getFormalParameters().getFormalParameter(i).getName();
            Object value = subExpressionResults.get(i);
            context.writeSymbol(parameterName, value);
        }
        setState(DOING_STATEMENTS);
        return fun.getSubStatements().get(0);
    }

    @Override
    boolean handleDescendantResultDoingStatements(Object value) {
        functionResult = value;
        return true;
    }


    @Override
    AstNode stepDoingStatements(ExecutionContext<Object> context) {
        context.onResult(functionResult);
        setState(DONE);
        return null;
    }
}
