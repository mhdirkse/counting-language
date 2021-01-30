package com.github.mhdirkse.countlang.analysis;

class CodeBlockFunction extends CodeBlockSerial {
    CodeBlockFunction(CodeBlock parent) {
        super(parent);
    }

    @Override
    boolean isRootOrFunction() {
        return true;
    }
}
