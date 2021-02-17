package com.github.mhdirkse.countlang.analysis;

import java.util.stream.Collectors;

class CodeBlockParallel extends CodeBlock {
    CodeBlockParallel(CodeBlock parent) {
        super(parent);
    }

    @Override
    ReturnStatus getSpecificReturnStatus() {
        if(getNonSubfunctionChildren().isEmpty()) {
            throw new IllegalStateException("A CodeBlockParallel is assumed to have children because it branches");
        }
        ReturnStatus lowest = getNonSubfunctionChildren().stream().map(CodeBlock::getReturnStatus).collect(Collectors.minBy(ReturnStatus.COMPARATOR)).get();
        ReturnStatus highest = getNonSubfunctionChildren().stream().map(CodeBlock::getReturnStatus).collect(Collectors.maxBy(ReturnStatus.COMPARATOR)).get();
        ReturnStatus result = null;
        if(highest == ReturnStatus.NONE_RETURN) {
            result = ReturnStatus.NONE_RETURN;
        } else if(lowest == ReturnStatus.STRONG_ALL_RETURN) {
            result = ReturnStatus.STRONG_ALL_RETURN;
        } else if(lowest == ReturnStatus.WEAK_ALL_RETURN) {
            result = ReturnStatus.WEAK_ALL_RETURN;
        } else {
            result = ReturnStatus.SOME_RETURN;
        }
        return result;
    }

    @Override
    boolean isRootOrFunction() {
        return false;
    }

    @Override
    StatementHandler handleReturn(int line, int column) {
        throw new IllegalStateException("A branch is always followed by a new branch or by ending the switch, so a return statement cannot appear here");
    }
}
