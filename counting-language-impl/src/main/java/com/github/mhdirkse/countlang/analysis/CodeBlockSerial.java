package com.github.mhdirkse.countlang.analysis;

import java.util.Optional;
import java.util.stream.Collectors;

abstract class CodeBlockSerial extends CodeBlock {
    CodeBlockSerial(CodeBlock parent) {
        super(parent);
    }

    CodeBlockSerial() {
        super();
    }

    @Override
    ReturnStatus getSpecificReturnStatus() {
        if(! getReturnStatements().isEmpty()) {
            return ReturnStatus.STRONG_ALL_RETURN;
        } else {
            Optional<ReturnStatus> opt = getNonSubfunctionChildren().stream()
                    .map(CodeBlock::getReturnStatus).collect(Collectors.maxBy(ReturnStatus.COMPARATOR));
            return opt.orElse(ReturnStatus.NONE_RETURN);
        }
    }
}
