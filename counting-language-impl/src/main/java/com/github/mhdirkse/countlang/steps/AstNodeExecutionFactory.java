package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.AstNode;

interface AstNodeExecutionFactory {
    AstNodeExecution create(AstNode node);
}
