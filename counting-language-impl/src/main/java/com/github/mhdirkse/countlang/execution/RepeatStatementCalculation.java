/*
 * Copyright Martijn Dirkse 2022
 *
 * This file is part of counting-language.
 *
 * counting-language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
