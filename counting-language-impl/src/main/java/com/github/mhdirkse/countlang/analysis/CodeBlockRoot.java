package com.github.mhdirkse.countlang.analysis;

import java.util.List;
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
        getAllVariableWrites().forEach(w -> w.report(reporter));
        if(getReturnStatus() != ReturnStatus.NONE_RETURN) {
            CodeBlockEvent.Return offending = Stream.concat(
                    getReturnStatements().stream(),
                    getNonSubfunctionChildren().stream().flatMap(b -> b.getReturnStatements().stream()))
                    .collect(Collectors.minBy(CodeBlockEvent.Return.COMPARATOR)).get();
            reporter.report(StatusCode.RETURN_OUTSIDE_FUNCTION, offending.getLine(), offending.getColumn());
        }
        getDescendants().stream()
            .filter(b -> b instanceof CodeBlockFunctionBase)
            .map(b -> (CodeBlockFunctionBase) b)
            .forEach(b -> b.report(reporter));
    }

    List<VariableWrite> getAllVariableWrites() {
        return Stream.concat(getVariableWrites().stream(), getDescendants().stream().flatMap(b -> b.getVariableWrites().stream()))
                .collect(Collectors.toList());
    }
}
