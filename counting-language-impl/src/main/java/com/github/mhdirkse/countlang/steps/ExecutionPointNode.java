package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.AstNode;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
class ExecutionPointNode {
    private final AstNodeExecutionState state;
    private final AstNode node;

    ExecutionPointNode(final AstNode node, final AstNodeExecutionState state) {
        this.state = state;
        this.node = node;
    }
}
