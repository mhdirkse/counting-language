package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.ExpressionsAndStatementsCombinationHandler.State.DOING_EXPRESSIONS;
import static com.github.mhdirkse.countlang.steps.ExpressionsAndStatementsCombinationHandler.State.DOING_STATEMENTS;
import static com.github.mhdirkse.countlang.steps.ExpressionsAndStatementsCombinationHandler.State.DONE;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.IfStatement;

class IfStatementCalculation extends ExpressionsAndStatementsCombinationHandler {
    private final IfStatement ifStatement;
    Boolean selectorValue = null;

    IfStatementCalculation(IfStatement ifStatement) {
        this.ifStatement = ifStatement;
    }

    @Override
    public AstNode getAstNode() {
        return ifStatement;
    }

    @Override
    AstNode stepBefore(ExecutionContext context) {
        setState(DOING_EXPRESSIONS);
        return ifStatement.getSelector();
    }

    @Override
    boolean handleDescendantResultDoingExpressions(Object value) {
        selectorValue = (Boolean) value;
        return true;
    }

    @Override
    AstNode stepDoingExpressions(ExecutionContext context) {
        setState(DOING_STATEMENTS);
        if(selectorValue.booleanValue()) {
            return ifStatement.getThenStatement();
        } else if(ifStatement.getElseStatement() != null) {
            return ifStatement.getElseStatement();
        } else {
            return null;
        }
    }

    @Override
    boolean handleDescendantResultDoingStatements(Object value, ExecutionContext context) {
        setState(DONE);
        return false;
    }

    @Override
    AstNode stepDoingStatements(ExecutionContext context) {
        setState(DONE);
        return null;
    }
}
