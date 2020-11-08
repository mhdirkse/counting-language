package com.github.mhdirkse.countlang.steps;

import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.BEFORE;
import static com.github.mhdirkse.countlang.steps.AstNodeExecutionState.AFTER;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;

final class FunctionDefinitionStatementCalculation implements AstNodeExecution {
    private final FunctionDefinitionStatement statement;
    private AstNodeExecutionState state = BEFORE;
    
    FunctionDefinitionStatementCalculation(final FunctionDefinitionStatement statement) {
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
