package com.github.mhdirkse.countlang.analysis;

import com.github.mhdirkse.countlang.tasks.StatusReporter;

class CodeUnitNested extends CodeUnit {
    @Override
    boolean checkFunctionOrExperimentDefinitionAllowed(StatusReporter reporter) {
        // TODO: Error nested functions not allowed.
        return false;
    }
}
