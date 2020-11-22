package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.AstNode;

interface AstNodeExecution {
    AstNode getAstNode();
    AstNodeExecutionState getState();

    default ExecutionPointNode getExecutionPointNode() {
        return new ExecutionPointNode(getAstNode(), getState());
    }

    AstNode step(ExecutionContext context);

    default boolean isAcceptingChildResults() {
        return false;
    }

    default void acceptChildResult(Object value, ExecutionContext context) {
        throw new IllegalStateException("Programming error. Did not expect child result");
    }

    default AstNodeExecution fork() {
        throw new IllegalStateException("Fork only happens in sample statements or experiment definitions; this element should not be in the call stack then");
    }
}
