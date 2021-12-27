package com.github.mhdirkse.countlang.execution;

import static com.github.mhdirkse.countlang.execution.AstNodeExecutionState.AFTER;
import static com.github.mhdirkse.countlang.execution.AstNodeExecutionState.BEFORE;

import com.github.mhdirkse.countlang.ast.AstNode;

abstract class AbstractSampleStatementCalculationForked implements AstNodeExecution {
    final Object value;
    final AstNode sampleStatement;
    private AstNodeExecutionState state = BEFORE;

    AbstractSampleStatementCalculationForked(AbstractSampleStatementCalculation orig) {
        this.value = orig.value;
        this.sampleStatement = orig.getAstNode();
    }

    @Override
    public AstNode getAstNode() {
        return sampleStatement;
    }

    @Override
    public AstNodeExecutionState getState() {
        return state;
    }

    @Override
    public AstNode step(ExecutionContext context) {
        if(state == BEFORE) {
            assign(context);
        	state = AFTER;
        }
        return null;
    }

    abstract void assign(ExecutionContext context);
}
