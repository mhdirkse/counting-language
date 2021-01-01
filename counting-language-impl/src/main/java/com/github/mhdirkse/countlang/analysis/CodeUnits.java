package com.github.mhdirkse.countlang.analysis;

import com.github.mhdirkse.countlang.tasks.StatusReporter;
import com.github.mhdirkse.countlang.utils.Stack;

class CodeUnits {
    private Stack<CodeUnit> codeUnits;

    void push(CodeUnit codeUnit) {
        codeUnits.push(codeUnit);
    }

    void pop() {
        codeUnits.pop();
    }

    void onEvent(ControlEvent controlEvent) {
        codeUnits.peek().onEvent(controlEvent);
    }

    boolean checkFunctionOrExperimentDefinitionAllowed(StatusReporter reporter) {
        return codeUnits.peek().checkFunctionOrExperimentDefinitionAllowed(reporter);
    }
}
