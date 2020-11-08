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
    public final boolean isAcceptingChildResults() {
        switch(state) {
        case DOING_EXPRESSIONS:
            return true;
        case DOING_STATEMENTS:
            return isAcceptingChildResultsDoingStatements();
        default:
            return false;
        }
    }

    @Override
    public final void acceptChildResult(Object value, ExecutionContext context) {
        switch(state) {
        case DOING_EXPRESSIONS:
            acceptChildResultDoingExpressions(value, context);
            break;
        case DOING_STATEMENTS:
            acceptChildResultDoingStatements(value, context);
            break;
        default:
            throw new IllegalStateException("Programming error: Unexpected child result");
        }
    }

    abstract AstNode stepBefore(ExecutionContext context);
    abstract AstNode stepDoingExpressions(ExecutionContext context);
    abstract AstNode stepDoingStatements(ExecutionContext context);
    abstract boolean isAcceptingChildResultsDoingStatements();
    abstract void acceptChildResultDoingExpressions(Object value, ExecutionContext context);
    abstract void acceptChildResultDoingStatements(Object value, ExecutionContext context);
}
