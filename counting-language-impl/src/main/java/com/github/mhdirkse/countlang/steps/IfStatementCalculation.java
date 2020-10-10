package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.BEFORE;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.RUNNING;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.AFTER;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.IfStatement;

class IfStatementCalculation implements AstNodeExecution<Object> {
    private final IfStatement ifStatement;
    private AstNodeExecutionState state = BEFORE;
    Boolean selectorValue = null;

    IfStatementCalculation(IfStatement ifStatement) {
        this.ifStatement = ifStatement;
    }

    @Override
    public AstNode getAstNode() {
        return ifStatement;
    }

    @Override
    public AstNodeExecutionState getState() {
        return state;
    }

    @Override
    public AstNode step(ExecutionContext<Object> context) {
        state = RUNNING;
        if(selectorValue == null) {
            return ifStatement.getSelector();
        }
        else {
            if(selectorValue.booleanValue()) {
                return ifStatement.getThenStatement();
            } else if(ifStatement.getElseStatement() != null) {
                return ifStatement.getElseStatement();
            }
            state = AFTER;
            return null;
        }
    }

    @Override
    public boolean handleDescendantResult(Object value, ExecutionContext<Object> context) {
        if(selectorValue == null) {
            selectorValue = (Boolean) value;
            return true;
        }
        else {
            state = AFTER;
            return false;
        }
    }
}
