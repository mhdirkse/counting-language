package com.github.mhdirkse.countlang.analysis;

import java.util.EnumSet;
import java.util.stream.Stream;

import com.github.mhdirkse.countlang.tasks.StatusCode;
import com.github.mhdirkse.countlang.tasks.StatusReporter;

import lombok.Getter;

abstract class CodeBlockFunctionBase extends CodeBlockSerial {
    private static final EnumSet<ReturnStatus> MISSING_RETURN = EnumSet.<ReturnStatus>of(ReturnStatus.NONE_RETURN, ReturnStatus.SOME_RETURN);
    int line;
    int column;
    final @Getter String functionName;

    CodeBlockFunctionBase(CodeBlock parent, int line, int column, final String functionName) {
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
        Stream.concat(Stream.of(this), getDescendants().stream().filter(b -> ! b.isRootOrFunction()))
            .forEach(b -> b.reportStatementHasNoEffect(reporter, functionName));
        reportMissingReturn(reporter);
    }

    abstract void reportMissingReturn(StatusReporter reporter);

    static class Function extends CodeBlockFunctionBase {
        Function(CodeBlock parent, int line, int column, final String functionName) {
            super(parent, line, column, functionName);
        }

        @Override
        void reportMissingReturn(StatusReporter reporter) {
            if(MISSING_RETURN.contains(getReturnStatus())) {
                reporter.report(StatusCode.FUNCTION_DOES_NOT_RETURN, line, column, functionName);
            }
        }
    }

    static class Experiment extends CodeBlockFunctionBase {
        Experiment(CodeBlock parent, int line, int column, final String functionName) {
            super(parent, line, column, functionName);
        }

        @Override
        void reportMissingReturn(StatusReporter reporter) {
        }
    }
}
