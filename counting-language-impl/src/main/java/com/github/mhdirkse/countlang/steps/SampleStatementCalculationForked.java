package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.BEFORE;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.AFTER;

import com.github.mhdirkse.countlang.ast.AstNode;

class SampleStatementCalculationForked implements AstNodeExecution {
    private final String symbol;
    private final Object value;
    private final AstNode node;
    private AstNodeExecutionState state = BEFORE;

    SampleStatementCalculationForked(SampleStatementCalculation orig) {
        this.symbol = orig.statement.getSymbol();
        this.value = orig.value;
        this.node = orig.getAstNode();
    }

    @Override
    public AstNode getAstNode() {
        return node;
    }


    @Override
    public AstNodeExecutionState getState() {
        return state;
    }

    @Override
    public AstNode step(ExecutionContext context) {
        if(state == BEFORE) {
            context.writeSymbol(symbol, value, node);
            state = AFTER;
        }
        return null;
    }
}
