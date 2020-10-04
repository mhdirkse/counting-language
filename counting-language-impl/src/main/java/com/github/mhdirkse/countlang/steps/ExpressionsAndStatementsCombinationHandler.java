package com.github.mhdirkse.countlang.steps;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CountlangType;

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

        static <S> BiFunction<ExpressionsAndStatementsCombinationHandler<S>, ExecutionContext<S>, AstNode> ignore() {
            return (v, c) -> null;
        }

        static <S> BiFunction<ExpressionsAndStatementsCombinationHandler<S>, S, Boolean> unexpected(State state) {
            return (visitor, value) -> {throw new IllegalArgumentException(
                    String.format("Unexpected descendant result for state %s, value %s",
                            state.toString(), value.toString()));};
            };
    }

    private final Map<State, BiFunction<ExpressionsAndStatementsCombinationHandler<T>, ExecutionContext<T>, AstNode>> stepDelegates;
    private final Map<State, BiFunction<ExpressionsAndStatementsCombinationHandler<T>, T, Boolean>> descendantDelegates;
    private State state = State.BEFORE;

    ExpressionsAndStatementsCombinationHandler(
            final Map<State, BiFunction<ExpressionsAndStatementsCombinationHandler<T>, ExecutionContext<T>, AstNode>> stepDelegates,
            final Map<State, BiFunction<ExpressionsAndStatementsCombinationHandler<T>, T, Boolean>> descendantDelegates) {
        this.stepDelegates = stepDelegates;
        this.descendantDelegates = descendantDelegates;
    }

    @Override
    public AstNodeExecutionState getState() {
        return state.getGeneralState();
    }

    void setState(final State state) {
        this.state = state;
    }

    @Override
    public AstNode step(ExecutionContext<T> context) {
        return stepDelegates.get(state).apply(this, context);
    }

    @Override
    public boolean handleDescendantResult(T value) {
        return descendantDelegates.get(state).apply(this, value);
    }

    abstract AstNode stepBefore(ExecutionContext<T> context);
    abstract AstNode stepDoingExpressions(ExecutionContext<T> context);
    abstract AstNode stepDoingStatements(ExecutionContext<T> context);
    abstract boolean handleDescendantResultDoingExpressions(T value);
    abstract boolean handleDescendantResultDoingStatements(T value);

    static abstract class TypeCheck extends ExpressionsAndStatementsCombinationHandler<CountlangType> {
        private static Map<State, BiFunction<ExpressionsAndStatementsCombinationHandler<CountlangType>, ExecutionContext<CountlangType>, AstNode>> getStepDelegates() {
            Map<State, BiFunction<ExpressionsAndStatementsCombinationHandler<CountlangType>, ExecutionContext<CountlangType>, AstNode>> result = new HashMap<>();
            result.put(State.BEFORE,
                    ExpressionsAndStatementsCombinationHandler<CountlangType>::stepBefore);
            result.put(State.DOING_EXPRESSIONS,
                    ExpressionsAndStatementsCombinationHandler<CountlangType>::stepDoingExpressions);
            result.put(State.DOING_STATEMENTS,
                    ExpressionsAndStatementsCombinationHandler<CountlangType>::stepDoingStatements);
            result.put(State.DONE, State.ignore());
            return result;
        }

        private static Map<State, BiFunction<ExpressionsAndStatementsCombinationHandler<CountlangType>, CountlangType, Boolean>> getDescendantDelegates() {
            Map<State, BiFunction<ExpressionsAndStatementsCombinationHandler<CountlangType>, CountlangType, Boolean>> result = new HashMap<>();
            result.put(State.BEFORE, State.unexpected(State.BEFORE));
            result.put(State.DOING_EXPRESSIONS,
                    ExpressionsAndStatementsCombinationHandler<CountlangType>::handleDescendantResultDoingExpressions);
            result.put(State.DOING_STATEMENTS,
                    ExpressionsAndStatementsCombinationHandler<CountlangType>::handleDescendantResultDoingStatements);
            result.put(State.DONE, State.unexpected(State.DONE));
            return result;
        }

        TypeCheck() {
            super(getStepDelegates(), getDescendantDelegates());
        }
    }

    static abstract class Calculation extends ExpressionsAndStatementsCombinationHandler<Object> {
        private static Map<State, BiFunction<ExpressionsAndStatementsCombinationHandler<Object>, ExecutionContext<Object>, AstNode>> getStepDelegates() {
            Map<State, BiFunction<ExpressionsAndStatementsCombinationHandler<Object>, ExecutionContext<Object>, AstNode>> result = new HashMap<>();
            result.put(State.BEFORE,
                    ExpressionsAndStatementsCombinationHandler<Object>::stepBefore);
            result.put(State.DOING_EXPRESSIONS,
                    ExpressionsAndStatementsCombinationHandler<Object>::stepDoingExpressions);
            result.put(State.DOING_STATEMENTS,
                    ExpressionsAndStatementsCombinationHandler<Object>::stepDoingStatements);
            result.put(State.DONE, State.ignore());
            return result;
        }

        private static Map<State, BiFunction<ExpressionsAndStatementsCombinationHandler<Object>, Object, Boolean>> getDescendantDelegates() {
            Map<State, BiFunction<ExpressionsAndStatementsCombinationHandler<Object>, Object, Boolean>> result = new HashMap<>();
            result.put(State.BEFORE, State.unexpected(State.BEFORE));
            result.put(State.DOING_EXPRESSIONS,
                    ExpressionsAndStatementsCombinationHandler<Object>::handleDescendantResultDoingExpressions);
            result.put(State.DOING_STATEMENTS,
                    ExpressionsAndStatementsCombinationHandler<Object>::handleDescendantResultDoingStatements);
            result.put(State.DONE, State.unexpected(State.DONE));
            return result;
        }

        Calculation() {
            super(getStepDelegates(), getDescendantDelegates());
        }
    }
}
