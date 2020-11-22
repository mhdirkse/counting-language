package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.AFTER;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.BEFORE;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatementBase;

final class FunctionDefinitionStatementBaseCalculation implements AstNodeExecution {
    private final FunctionDefinitionStatementBase statement;
    private AstNodeExecutionState state = BEFORE;
    
    FunctionDefinitionStatementBaseCalculation(final FunctionDefinitionStatementBase statement) {
        this.statement = statement;
    }

    @Override
    public AstNode getAstNode() {
        return statement;
    }

    @Override
    public AstNodeExecutionState getState() {
        return state;
    }

    @Override
    public AstNode step(ExecutionContext context) {
        context.defineFunction(statement);
        state = AFTER;
        return null;
    }
}
