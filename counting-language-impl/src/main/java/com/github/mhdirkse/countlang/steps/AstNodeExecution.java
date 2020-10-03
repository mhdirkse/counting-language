package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.AstNode;

interface AstNodeExecution<T> {
    AstNode getAstNode();
    AstNodeExecutionState getState();

    default ExecutionPointNode getExecutionPointNode() {
        return new ExecutionPointNode(getAstNode(), getState());
    }

    AstNode step(ExecutionContext<T> context);
    boolean handleDescendantResult(T value);
}
