package com.github.mhdirkse.countlang.analysis;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.mhdirkse.countlang.tasks.StatusCode;
import com.github.mhdirkse.countlang.tasks.StatusReporter;

class CodeBlockRoot extends CodeBlockSerial {
    CodeBlockRoot() {
        super();
    }

    @Override
    boolean isRootOrFunction() {
        return true;
    }

    void report(final StatusReporter reporter) {
        if(getReturnStatus() != ReturnStatus.NONE_RETURN) {
            CodeBlockEvent.Return offending = Stream.concat(
                    getReturnStatements().stream(),
                    getNonSubfunctionChildren().stream().flatMap(b -> b.getReturnStatements().stream()))
                    .collect(Collectors.minBy(CodeBlockEvent.Return.COMPARATOR)).get();
            reporter.report(StatusCode.RETURN_OUTSIDE_FUNCTION, offending.getLine(), offending.getColumn());
        }
        getDescendants().stream()
            .filter(b -> b instanceof CodeBlockFunction)
            .map(b -> (CodeBlockFunction) b)
            .forEach(b -> b.report(reporter));
    }
}
