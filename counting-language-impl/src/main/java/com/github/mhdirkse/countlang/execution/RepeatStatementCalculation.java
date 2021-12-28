package com.github.mhdirkse.countlang.execution;

import static com.github.mhdirkse.countlang.execution.ExpressionsAndStatementsCombinationHandler.State.DOING_EXPRESSIONS;
import static com.github.mhdirkse.countlang.execution.ExpressionsAndStatementsCombinationHandler.State.DOING_STATEMENTS;
import static com.github.mhdirkse.countlang.execution.ExpressionsAndStatementsCombinationHandler.State.DONE;

import java.math.BigInteger;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.ProgramException;
import com.github.mhdirkse.countlang.ast.RepeatStatement;

class RepeatStatementCalculation extends ExpressionsAndStatementsCombinationHandler implements NeedsExplicitStop {
	private RepeatStatement statement;
	private BigInteger numRepetitions;
	private BigInteger numRepetitionsDone = BigInteger.ZERO;
	private boolean stopRequested = false;
	private boolean statementBeingExecuted = false;

	RepeatStatementCalculation(RepeatStatement statement) {
		this.statement = statement;
	}

	RepeatStatementCalculation(RepeatStatementCalculation orig) {
		super(orig);
		this.statement = orig.statement;
		this.numRepetitions = orig.numRepetitions;
		this.numRepetitionsDone = orig.numRepetitionsDone;
		this.stopRequested = orig.stopRequested;
		this.statementBeingExecuted = orig.statementBeingExecuted;
	}

	@Override
	public AstNode getAstNode() {
		return statement;
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
        if(numRepetitions == null) {
            return statement.getCountExpr();
        }
        if(numRepetitions.compareTo(BigInteger.ZERO) < 0) {
        	throw new ProgramException(statement.getLine(), statement.getColumn(), "Repeat statement cannot have a negative repeat count, got " + numRepetitions.toString());
        }
        setState(DOING_STATEMENTS);
        return stepDoingStatements(context);
    }

    @Override
    void acceptChildResultDoingExpressions(Object value, ExecutionContext context) {
        numRepetitions = (BigInteger) value;
    }

    @Override
    AstNode stepDoingStatements(ExecutionContext context) {
        if(statementBeingExecuted) {
        	statementBeingExecuted = false;
        	numRepetitionsDone = numRepetitionsDone.add(BigInteger.ONE);
        }
    	if(stopRequested || (numRepetitionsDone.compareTo(numRepetitions) >= 0)) {
            setState(DONE);
            return null;
        }
    	statementBeingExecuted = true;
        return statement.getStatement();
    }

    @Override
    boolean isAcceptingChildResultsDoingStatements() {
        return false;
    }

    @Override
    void acceptChildResultDoingStatements(Object value, ExecutionContext context) {
        throw new IllegalStateException("When the statements within a repeat statement are being executed, no child results are expected");
    }

    @Override
    public AstNodeExecution fork() {
        return new RepeatStatementCalculation(this);
    }

    @Override
    public void stopFunctionCall() {
        stopRequested = true;
    }
}
