package com.github.mhdirkse.countlang.analysis;

import com.github.mhdirkse.countlang.tasks.StatusReporter;

abstract class CodeUnit {
    int statementDepth = 0;

    void onEvent(ControlEvent controlEvent) {
        switch(controlEvent.getEventType()) {
        case STATEMENT_OPEN:
            statementDepth++;
            break;
        case STATEMENT_CLOSE:
            statementDepth--;
            break;
        default:
        }
    }

    abstract boolean checkFunctionOrExperimentDefinitionAllowed(StatusReporter reporter);
}
