package com.github.mhdirkse.countlang.analysis;

class CodeBlockSerialPart extends CodeBlockSerial {
    CodeBlockSerialPart(CodeBlock parent) {
        super(parent);
    }

    @Override
    boolean isRootOrFunction() {
        return false;
    }

    @Override
    void handleStatementAfterStopped(int line, int column) {
        throw new IllegalStateException("After the end of a branch, only a new branch or a switch stop is expected, no statement");
    }
}
