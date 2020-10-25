package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.AstNode;

interface AstNodeExecution {
    AstNode getAstNode();
    AstNodeExecutionState getState();

    default ExecutionPointNode getExecutionPointNode() {
        return new ExecutionPointNode(getAstNode(), getState());
    }

    AstNode step(ExecutionContext context);
    boolean handleDescendantResult(Object value, ExecutionContext context);
}
