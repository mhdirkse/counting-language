package com.github.mhdirkse.countlang.analysis;

class CodeBlockRepeated extends CodeBlockSerial {
    CodeBlockRepeated(CodeBlock parent) {
        super(parent);
    }

    @Override
    ReturnStatus getSpecificReturnStatus() {
        ReturnStatus result = super.getSpecificReturnStatus();
        if((result == ReturnStatus.SOME_RETURN) && (getStatementAfter() == null)) {
            result = ReturnStatus.WEAK_ALL_RETURN;
        }
        return result;
    }

    @Override
    public boolean isRootOrFunction() {
        return false;
    }
}
