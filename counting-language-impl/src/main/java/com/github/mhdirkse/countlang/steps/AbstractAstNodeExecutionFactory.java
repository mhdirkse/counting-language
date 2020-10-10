package com.github.mhdirkse.countlang.steps;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.ast.FormalParameters;
import com.github.mhdirkse.countlang.ast.Operator;
import com.github.mhdirkse.countlang.ast.Visitor;

abstract class AbstractAstNodeExecutionFactory<T> implements AstNodeExecutionFactory<T>, Visitor {
    AstNodeExecution<T> result;

    @Override
    public AstNodeExecution<T> create(AstNode node) {
        node.accept(this);
        AstNodeExecution<T> currentResult = result;
        result = null;
        return currentResult;
    }

    @Override
    public void visitOperator(Operator operator) {
    }

    @Override
    public void visitFormalParameters(FormalParameters formalParameters) {
    }

    @Override
    public void visitFormalParameter(FormalParameter formalParameter) {
    }
}
