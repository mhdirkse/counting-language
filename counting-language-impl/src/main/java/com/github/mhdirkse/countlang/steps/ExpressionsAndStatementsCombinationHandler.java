package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.AstNode;

abstract class ExpressionsAndStatementsCombinationHandler implements AstNodeExecution {
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
    public final AstNodeExecutionState getState() {
        return state.getGeneralState();
    }

    final void setState(final State state) {
        this.state = state;
    }

    @Override
    public final AstNode step(ExecutionContext context) {
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
    public final boolean handleDescendantResult(Object value, ExecutionContext context) {
        switch(state) {
        case BEFORE:
        case DONE:
            throw new IllegalArgumentException(
                    String.format("Unexpected descendant result for state %s, value %s",
                            state.toString(), value.toString()));
        case DOING_EXPRESSIONS:
            return handleDescendantResultDoingExpressions(value);
        default:
            return handleDescendantResultDoingStatements(value, context);
        }
    }

    abstract AstNode stepBefore(ExecutionContext context);
    abstract AstNode stepDoingExpressions(ExecutionContext context);
    abstract AstNode stepDoingStatements(ExecutionContext context);
    abstract boolean handleDescendantResultDoingExpressions(Object value);
    abstract boolean handleDescendantResultDoingStatements(Object value, ExecutionContext context);
}
