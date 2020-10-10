package com.github.mhdirkse.countlang.steps;

import java.util.List;

import com.github.mhdirkse.countlang.ast.CompositeNode;
import com.github.mhdirkse.countlang.execution.DummyValue;

class ExpressionResultCollectorVarUsage extends ExpressionResultsCollector<DummyValue> {
    static enum ResultStrategy {
        RESULT,
        NO_RESULT;
    }

    private final ResultStrategy resultStrategy;
    
    ExpressionResultCollectorVarUsage(CompositeNode node, ResultStrategy resultStrategy) {
        super(node);
        this.resultStrategy = resultStrategy;
    }

    @Override
    void processSubExpressionResults(List<DummyValue> subExpressionResults, ExecutionContext<DummyValue> context) {
        switch(resultStrategy) {
        case RESULT:
            context.onResult(DummyValue.getInstance());
            break;
        default:
            break;
        }
    }

    @Override
    boolean isDescendantResultHandled() {
        return true;
    }
}
