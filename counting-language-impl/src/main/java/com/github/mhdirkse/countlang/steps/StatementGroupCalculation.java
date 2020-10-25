package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.AFTER;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.BEFORE;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.RUNNING;

import java.util.List;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.StatementGroup;
import com.github.mhdirkse.countlang.execution.StackFrameAccess;

class StatementGroupCalculation implements AstNodeExecution {
    private final StatementGroup statementGroup;
    AstNodeExecutionState state = BEFORE;
    private List<AstNode> children;
    private int childIndex = 0;

    StatementGroupCalculation(StatementGroup statementGroup) {
        this.statementGroup = statementGroup;
        this.children = statementGroup.getChildren();
    }

    @Override
    public AstNode getAstNode() {
        return statementGroup;
    }

    @Override
    public AstNodeExecutionState getState() {
        return state;
    }

    @Override
    public AstNode step(ExecutionContext context) {
        if(state == AFTER) {
            return null;
        }
        if(state == BEFORE) {
            context.pushVariableFrame(StackFrameAccess.SHOW_PARENT);
        }
        state = RUNNING;
        if(childIndex < children.size()) {
            return children.get(childIndex++);
        }
        done(context);
        return null;
    }

    void done(ExecutionContext context) {
        context.popVariableFrame();
        state = AFTER;
    }

    @Override
    public boolean handleDescendantResult(Object value, ExecutionContext context) {
        done(context);
        return false;
    }
}
