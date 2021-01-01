package com.github.mhdirkse.countlang.analysis;

import com.github.mhdirkse.countlang.ast.ExperimentDefinitionStatement;

class CodeUnitExperiment extends CodeUnitNested {
    private final ExperimentDefinitionStatement expDef;

    CodeUnitExperiment(ExperimentDefinitionStatement expDef) {
        this.expDef = expDef;
    }
}
