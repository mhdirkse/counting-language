package com.github.mhdirkse.countlang.analysis;

import com.github.mhdirkse.countlang.ast.FunctionDefinitionStatement;

class CodeUnitFunction extends CodeUnitNested {
    private final FunctionDefinitionStatement funDef;

    CodeUnitFunction(FunctionDefinitionStatement funDef) {
        this.funDef = funDef;
    }
}
