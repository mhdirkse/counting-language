package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.AFTER;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.BEFORE;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.RUNNING;

import java.util.List;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.execution.DummyValue;

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
        if(state == AFTER) {
            return null;
        }
        if(state == BEFORE) {
            context.pushVariableFrame();
        }
        state = RUNNING;
        if(childIndex < children.size()) {
            beforeChildEntered(children.get(childIndex), context);
            return children.get(childIndex++);
        }
        done(context);
        return null;
    }

    void done(ExecutionContext<T> context) {
        context.popVariableFrame();
        state = AFTER;
    }

    @Override
    public boolean handleDescendantResult(T value, ExecutionContext<T> context) {
        onReturnEncountered(context);
        return false;
    }

    abstract void beforeChildEntered(AstNode child, ExecutionContext<T> context);
    abstract void onReturnEncountered(ExecutionContext<T> context);

    static class TypeCheck extends StatementGroupHandler<CountlangType> {
        TypeCheck(StatementGroup statementGroup) {
            super(statementGroup);
        }

        @Override
        void beforeChildEntered(AstNode child, ExecutionContext<CountlangType> context) {
            context.onStatement(child);
        }

        @Override
        void onReturnEncountered(ExecutionContext<CountlangType> context) {
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
        void onReturnEncountered(ExecutionContext<Object> context) {
            done(context);
        }
    }

    static class VarUsage extends StatementGroupHandler<DummyValue> {
        VarUsage(StatementGroup statementGroup) {
            super(statementGroup);
        }

        @Override
        void beforeChildEntered(AstNode child, ExecutionContext<DummyValue> context) {
        }

        @Override
        void onReturnEncountered(ExecutionContext<DummyValue> context) {
        }
    }
}
