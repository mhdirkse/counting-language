package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.ExpressionsAndStatementsCombinationHandler.State.DOING_EXPRESSIONS;
import static com.github.mhdirkse.countlang.steps.ExpressionsAndStatementsCombinationHandler.State.DOING_STATEMENTS;
import static com.github.mhdirkse.countlang.steps.ExpressionsAndStatementsCombinationHandler.State.DONE;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.FunctionCallExpression;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;

class FunctionCallExpressionTypeCheck extends ExpressionsAndStatementsCombinationHandler.TypeCheck {
    private final FunctionCallExpression expression;
    private final SubExpressionStepper<CountlangType> subExpressionStepper;
    private final List<CountlangType> subExpressionResults = new ArrayList<>();
    private FunctionDefinitionStatement fun;
    private Set<CountlangType> returnTypeCandidates = new HashSet<>();

    FunctionCallExpressionTypeCheck(final FunctionCallExpression expression) {
        this.expression = expression;
        this.subExpressionStepper = new SubExpressionStepper<CountlangType>(expression.getSubExpressions());
    }

    @Override
    public AstNode getAstNode() {
        return expression;
    }

    @Override
    AstNode stepBefore(ExecutionContext<CountlangType> context) {
        setState(DOING_EXPRESSIONS);
        return subExpressionStepper.step(context);
    }

    @Override
    boolean handleDescendantResultDoingExpressions(CountlangType value) {
        subExpressionResults.add(value);
        return true;
    }

    @Override
    AstNode stepDoingExpressions(ExecutionContext<CountlangType> context) {
        if(!subExpressionStepper.isDone()) {
            return subExpressionStepper.step(context);
        }
        if(!context.hasFunction(expression.getFunctionName())) {
            // TODO: Report that function does not exist.
            setState(DONE);
            return null;
        }
        fun = context.getFunction(expression.getFunctionName());
        if(!checkArguments(context)) {
            setState(DONE);
            return null;
        }
        setState(DOING_STATEMENTS);
        return fun.getSubStatements().get(0);
    }

    private boolean checkArguments(ExecutionContext<CountlangType> context) {
        // TODO: Check here that the argument count and the argument types match the function definition.
        return true;
    }

    @Override
    boolean handleDescendantResultDoingStatements(CountlangType value) {
        returnTypeCandidates.add(value);
        return true;
    }

    @Override
    AstNode stepDoingStatements(ExecutionContext<CountlangType> context) {
        setState(DONE);
        if(returnTypeCandidates.size() != 1) {
            // TODO: Report ambiguous return type.
        }
        context.onResult(returnTypeCandidates.iterator().next());
        return null;
    }
}
