package com.github.mhdirkse.countlang.steps;

import java.util.List;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.ReturnStatement;

abstract class ReturnStatementHandler<T> extends ExpressionResultsCollector<T> {
    ReturnStatementHandler(ReturnStatement statement) {
        super(statement);
    }

    @Override
    boolean isDescendantResultHandled() {
        return false;
    }

    static class Analysis extends ReturnStatementHandler<CountlangType> {
        Analysis(ReturnStatement statement) {
            super(statement);
        }

        @Override
        void processSubExpressionResults(List<CountlangType> subExpressionResults, ExecutionContext<CountlangType> context) {
            context.onReturn((AstNode) node);
        }
    }

    static class Calculation extends ReturnStatementHandler<Object> {
        Calculation(ReturnStatement statement) {
            super(statement);
        }

        @Override
        void processSubExpressionResults(List<Object> subExpressionResults, ExecutionContext<Object> context) {
        }
    }
}
