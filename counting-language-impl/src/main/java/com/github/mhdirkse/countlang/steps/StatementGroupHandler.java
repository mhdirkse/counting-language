package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.AFTER;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.BEFORE;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.RUNNING;

import java.util.List;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.StatementGroup;

abstract class StatementGroupHandler<T> implements AstNodeExecution<T> {
    private final StatementGroup statementGroup;
    AstNodeExecutionState state = BEFORE;
    private List<AstNode> children;
    private int childIndex = 0;

    StatementGroupHandler(StatementGroup statementGroup) {
        this.statementGroup = statementGroup;
        this.children = statementGroup.getChildren();
    }

    @Override
    public AstNode getAstNode() {
        return statementGroup;
    }

    @Override
    public AstNodeExecutionState getState() {
        return state;
    }

    @Override
    public AstNode step(ExecutionContext<T> context) {
        state = RUNNING;
        if(childIndex < children.size()) {
            beforeChildEntered(children.get(childIndex), context);
            return children.get(childIndex++);
        }
        state = AFTER;
        return null;
    }

    @Override
    public boolean handleDescendantResult(T value) {
        onReturnEncountered();
        return false;
    }

    abstract void beforeChildEntered(AstNode child, ExecutionContext<T> context);
    abstract void onReturnEncountered();

    static class Analysis extends StatementGroupHandler<CountlangType> {
        Analysis(StatementGroup statementGroup) {
            super(statementGroup);
        }

        @Override
        void beforeChildEntered(AstNode child, ExecutionContext<CountlangType> context) {
            context.onStatement(child);
        }

        @Override
        void onReturnEncountered() {
        }
    }

    static class Calculation extends StatementGroupHandler<Object> {
        Calculation(StatementGroup statementGroup) {
            super(statementGroup);
        }

        @Override
        void beforeChildEntered(AstNode child, ExecutionContext<Object> context) {
        }

        @Override
        void onReturnEncountered() {
            state = AFTER;
        }
    }
}
