package com.github.mhdirkse.countlang.analysis;

import com.github.mhdirkse.countlang.tasks.StatusReporter;

class CodeUnitGlobal extends CodeUnit {
    @Override
    boolean checkFunctionOrExperimentDefinitionAllowed(StatusReporter reporter) {
        if(statementDepth == 1) {
            return true;
        } else {
            // Error: Function or experiment definition only allowed outside group
            return false;
        }
    }
}
