package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.AFTER;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.RUNNING;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.ExpressionNode;
import com.github.mhdirkse.countlang.ast.IfStatement;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.tasks.StatusCode;

class IfStatementTypeCheck implements AstNodeExecution<CountlangType> {
    private enum State {
        BEFORE(IfStatementTypeCheck::stepBefore),
        DOING_SELECTOR(IfStatementTypeCheck::stepDoingSelector),
        DOING_STATEMENTS(IfStatementTypeCheck::stepDoingStatements),
        DONE((tc, context) -> null);

        BiFunction<IfStatementTypeCheck, ExecutionContext<CountlangType>, AstNode> runner;

        State(BiFunction<IfStatementTypeCheck, ExecutionContext<CountlangType>, AstNode> runner) {
            this.runner = runner;
        }
    }

    private final IfStatement ifStatement;
    private final SubExpressionStepper<CountlangType> selectorHandler;
    private final List<StatementGroup> statementGroups;
    State state = State.BEFORE;
    private int statementGroupIndex = 0;

    IfStatementTypeCheck(IfStatement ifStatement) {
        this.ifStatement = ifStatement;
        this.selectorHandler = new SubExpressionStepper<CountlangType>(ifStatement.getSubExpressions());
        List<AstNode> childNodes = ifStatement.getChildren();
        this.statementGroups = childNodes.subList(1, childNodes.size()).stream()
                .map(c -> (StatementGroup) c)
                .collect(Collectors.toList());
    }

    @Override
    public AstNode getAstNode() {
        return ifStatement;
    }

    @Override
    public AstNode step(ExecutionContext<CountlangType> context) {
        return state.runner.apply(this, context);
    }

    public AstNode stepBefore(ExecutionContext<CountlangType> context) {
        state = State.DOING_SELECTOR;
        return selectorHandler.step(context);
    }

    public AstNode stepDoingSelector(ExecutionContext<CountlangType> context) {
        if(!selectorHandler.getSubExpressionResults().get(0).equals(CountlangType.BOOL)) {
            reportSelectorNotBoolean((ExpressionNode) ifStatement.getSelector(), context);
        }
        state = State.DOING_STATEMENTS;
        return statementGroups.get(statementGroupIndex++);        
    }

    public AstNode stepDoingStatements(ExecutionContext<CountlangType> context) {
        if(statementGroupIndex < statementGroups.size()) {
            return statementGroups.get(statementGroupIndex++);
        }
        else {
            state = State.DONE;
        }
        return null;        
    }

    @Override
    public AstNodeExecutionState getState() {
        switch(state) {
        case BEFORE:
            return AstNodeExecutionState.BEFORE;
        case DOING_SELECTOR:
        case DOING_STATEMENTS:
            return RUNNING;
        default:
            return AFTER;
        }
    }

    @Override
    public boolean handleDescendantResult(CountlangType value) {
        if(state == State.DOING_SELECTOR) {
            selectorHandler.handleDescendantResult(value);
            return true;
        }
        else {
            return false;
        }
    }

    void reportSelectorNotBoolean(ExpressionNode selector, ExecutionContext<CountlangType> context) {
        context.report(
                StatusCode.IF_SELECT_NOT_BOOLEAN,
                selector.getLine(),
                selector.getColumn(),
                selector.getCountlangType().toString());

    }
}
