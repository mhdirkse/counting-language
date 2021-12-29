package com.github.mhdirkse.countlang.execution;

import static com.github.mhdirkse.countlang.execution.ExpressionsAndStatementsCombinationHandler.State.DOING_EXPRESSIONS;
import static com.github.mhdirkse.countlang.execution.ExpressionsAndStatementsCombinationHandler.State.DOING_STATEMENTS;
import static com.github.mhdirkse.countlang.execution.ExpressionsAndStatementsCombinationHandler.State.DONE;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.ForInRepetitionStatement;
import com.github.mhdirkse.countlang.type.CountlangArray;

class ForInRepetitionStatementCalculation extends ExpressionsAndStatementsCombinationHandler implements NeedsExplicitStop {
	private ForInRepetitionStatement statement;
	private CountlangArray items;
	private int itemIndex = 0;
	private boolean stopRequested = false;
	private boolean statementBeingExecuted = false;

	ForInRepetitionStatementCalculation(ForInRepetitionStatement statement) {
		this.statement = statement;
	}

	ForInRepetitionStatementCalculation(ForInRepetitionStatementCalculation orig) {
		super(orig);
		this.statement = orig.statement;
		this.items = orig.items;
		this.itemIndex = orig.itemIndex;
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
        if(items == null) {
        	return statement.getFromArray();
        }
        setState(DOING_STATEMENTS);
        return stepDoingStatements(context);
    }

    @Override
    void acceptChildResultDoingExpressions(Object value, ExecutionContext context) {
        items = (CountlangArray) value;
    }

    @Override
    AstNode stepDoingStatements(ExecutionContext context) {
        if(statementBeingExecuted) {
        	statementBeingExecuted = false;
        	++itemIndex;
        }
    	if(stopRequested || (itemIndex >= items.size())) {
            setState(DONE);
            return null;
        }
    	statementBeingExecuted = true;
        VariableAssigner assigner = new VariableAssigner(context, statement.getFromArray(), items.get(itemIndex));
        assigner.assign(statement.getLhs());
        return statement.getStatement();
    }

    @Override
    boolean isAcceptingChildResultsDoingStatements() {
        return false;
    }

    @Override
    void acceptChildResultDoingStatements(Object value, ExecutionContext context) {
        throw new IllegalStateException("When the statements within a for ... in  statement are being executed, no child results are expected");
    }

    @Override
    public AstNodeExecution fork() {
        return new ForInRepetitionStatementCalculation(this);
    }

    @Override
    public void stopFunctionCall() {
        stopRequested = true;
    }
}
