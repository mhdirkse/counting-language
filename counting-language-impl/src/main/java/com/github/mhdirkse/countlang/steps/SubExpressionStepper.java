package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.AFTER;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.BEFORE;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.RUNNING;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.ExpressionNode;

class SubExpressionStepper {
    private List<ExpressionNode> subExpressions = null;
    private AstNodeExecutionState state;
    private int subExpressionIndex = 0;
    private List<Object> subExpressionResults = new ArrayList<>();

    SubExpressionStepper(final List<ExpressionNode> subExpressions) {
        this.subExpressions = subExpressions;
        this.state = BEFORE;
    }

    SubExpressionStepper(final SubExpressionStepper orig) {
        subExpressions = new ArrayList<>(orig.subExpressionIndex);
        this.state = orig.state;
        this.subExpressionIndex = orig.subExpressionIndex;
        subExpressionResults = new ArrayList<>(orig.subExpressionResults);
    }

    public AstNodeExecutionState getState() {
        return state;
    }

    public boolean isDone() {
        return state.equals(AFTER);
    }

    public AstNode step(ExecutionContext context) {
        if(state == AFTER) {
            return null;
        }
        state = RUNNING;
        if(subExpressionIndex < subExpressions.size()) {
            return subExpressions.get(subExpressionIndex++);
        }
        else {
            state = AFTER;
            return null;
        }
    }

    public void acceptChildResult(Object value) {
        subExpressionResults.add(value);
    }

    public List<Object> getSubExpressionResults() {
        return subExpressionResults;
    }
}
