package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.AstNode;

abstract class ExpressionsAndStatementsCombinationHandler<T> implements AstNodeExecution<T> {
    enum State {
        BEFORE(AstNodeExecutionState.BEFORE),
        DOING_EXPRESSIONS(AstNodeExecutionState.RUNNING),
        DOING_STATEMENTS(AstNodeExecutionState.RUNNING),
        DONE(AstNodeExecutionState.AFTER);

        private final AstNodeExecutionState generalState;

        State(AstNodeExecutionState generalState) {
            this.generalState = generalState;
        }

        AstNodeExecutionState getGeneralState() {
            return generalState;
        }
    }

    private State state = State.BEFORE;

    @Override
    public AstNodeExecutionState getState() {
        return state.getGeneralState();
    }

    void setState(final State state) {
        this.state = state;
    }

    @Override
    public AstNode step(ExecutionContext<T> context) {
        switch(state) {
        case BEFORE:
            return stepBefore(context);
        case DOING_EXPRESSIONS:
            return stepDoingExpressions(context);
        case DOING_STATEMENTS:
            return stepDoingStatements(context);
        default:
            return null;
        }
    }

    @Override
    public boolean handleDescendantResult(T value, ExecutionContext<T> context) {
        switch(state) {
        case BEFORE:
        case DONE:
            throw new IllegalArgumentException(
                    String.format("Unexpected descendant result for state %s, value %s",
                            state.toString(), value.toString()));
        case DOING_EXPRESSIONS:
            return handleDescendantResultDoingExpressions(value);
        default:
            return handleDescendantResultDoingStatements(value);
        }
    }

    abstract AstNode stepBefore(ExecutionContext<T> context);
    abstract AstNode stepDoingExpressions(ExecutionContext<T> context);
    abstract AstNode stepDoingStatements(ExecutionContext<T> context);
    abstract boolean handleDescendantResultDoingExpressions(T value);
    abstract boolean handleDescendantResultDoingStatements(T value);
}
