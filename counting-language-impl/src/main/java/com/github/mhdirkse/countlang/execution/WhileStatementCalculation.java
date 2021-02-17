package com.github.mhdirkse.countlang.execution;

import static com.github.mhdirkse.countlang.execution.ExpressionsAndStatementsCombinationHandler.State.DOING_EXPRESSIONS;
import static com.github.mhdirkse.countlang.execution.ExpressionsAndStatementsCombinationHandler.State.DOING_STATEMENTS;
import static com.github.mhdirkse.countlang.execution.ExpressionsAndStatementsCombinationHandler.State.DONE;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.WhileStatement;

class WhileStatementCalculation extends ExpressionsAndStatementsCombinationHandler implements NeedsExplicitStop {
    private final WhileStatement whileStatement;
    private Boolean testValue;
    private boolean stopRequested = false;

    WhileStatementCalculation(final WhileStatement whileStatement) {
        this.whileStatement = whileStatement;
    }

    WhileStatementCalculation(final WhileStatementCalculation orig) {
        super(orig);
        this.whileStatement = orig.whileStatement;
        this.testValue = orig.testValue;
        this.stopRequested = orig.stopRequested;
    }

    @Override
    public AstNode getAstNode() {
        return whileStatement;
    }

    @Override
    AstNode stepBefore(ExecutionContext context) {
        setState(DOING_EXPRESSIONS);
        return stepDoingExpressions(context);
    }

    @Override
    AstNode stepDoingExpressions(ExecutionContext context) {
        if(stopRequested) {
            setState(DONE);
            return null;
        }
        if(testValue == null) {
            return whileStatement.getTestExpr();
        }
        boolean currentTestValue = testValue.booleanValue();
        testValue = null;
        if(currentTestValue == true) {
            setState(DOING_STATEMENTS);
            return whileStatement.getStatement();
        } else {
            setState(DONE);
            return null;
        }
    }

    @Override
    void acceptChildResultDoingExpressions(Object value, ExecutionContext context) {
        testValue = (Boolean) value;
    }

    @Override
    AstNode stepDoingStatements(ExecutionContext context) {
        if(stopRequested) {
            setState(DONE);
            return null;
        }
        setState(DOING_EXPRESSIONS);
        return null;
    }

    @Override
    boolean isAcceptingChildResultsDoingStatements() {
        return false;
    }

    @Override
    void acceptChildResultDoingStatements(Object value, ExecutionContext context) {
        throw new IllegalStateException("When the statements within a while statement are being executed, no child results are expected");
    }

    @Override
    public AstNodeExecution fork() {
        return new WhileStatementCalculation(this);
    }

    @Override
    public void stopFunctionCall() {
        stopRequested = true;
    }
}
