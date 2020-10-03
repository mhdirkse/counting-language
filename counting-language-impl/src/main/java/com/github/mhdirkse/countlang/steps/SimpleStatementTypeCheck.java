package com.github.mhdirkse.countlang.steps;

import java.util.List;

import com.github.mhdirkse.countlang.ast.CompositeNode;
import com.github.mhdirkse.countlang.ast.CountlangType;

class SimpleStatementTypeCheck extends ExpressionResultsCollector <CountlangType> {
    SimpleStatementTypeCheck(CompositeNode node) {
        super(node);
    }

    @Override
    void processSubExpressionResults(
            List<CountlangType> subExpressionResults,
            ExecutionContext<CountlangType> context) {
    }

    @Override
    boolean isDescendantResultHandled() {
        return true;
    }
}
