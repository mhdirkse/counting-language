package com.github.mhdirkse.countlang.ast;

import java.util.List;
import java.util.stream.Collectors;

public interface CompositeNode {
    List<AstNode> getChildren();

    default List<ExpressionNode> getSubExpressions() {
        return getChildren().stream()
                .filter(ex -> ex instanceof ExpressionNode)
                .map(ex -> (ExpressionNode) ex)
                .collect(Collectors.toList());
    }

    default List<Statement> getSubStatements() {
        return getChildren().stream()
                .filter(st -> st instanceof Statement)
                .map(st -> (Statement) st)
                .collect(Collectors.toList());
    }
}
