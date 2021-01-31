package com.github.mhdirkse.countlang.analysis;

import java.util.EnumSet;

import com.github.mhdirkse.countlang.tasks.StatusCode;
import com.github.mhdirkse.countlang.tasks.StatusReporter;

import lombok.Getter;

class CodeBlockFunction extends CodeBlockSerial {
    private static final EnumSet<ReturnStatus> MISSING_RETURN = EnumSet.<ReturnStatus>of(ReturnStatus.NONE_RETURN, ReturnStatus.SOME_RETURN);
    private int line;
    private int column;
    private final @Getter String functionName;

    CodeBlockFunction(CodeBlock parent, int line, int column, final String functionName) {
        super(parent);
        this.functionName = functionName;
        this.line = line;
        this.column = column;
    }

    @Override
    boolean isRootOrFunction() {
        return true;
    }

    void report(StatusReporter reporter) {
        getDescendants().stream().filter(b -> ! b.isRootOrFunction()).forEach(b -> b.reportStatementHasNoEffect(reporter, functionName));
        if(MISSING_RETURN.contains(getReturnStatus())) {
            reporter.report(StatusCode.FUNCTION_DOES_NOT_RETURN, line, column, functionName);
        }
    }
}
