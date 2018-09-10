package com.github.mhdirkse.countlang.tasks;

import com.github.mhdirkse.countlang.execution.OutputStrategy;
import com.github.mhdirkse.utils.AbstractStatusCode;

class StatusReporterImpl implements StatusReporter {
    private final OutputStrategy output;
    private boolean hasErrors;

    StatusReporterImpl(final OutputStrategy output) {
        this.output = output;
        hasErrors = false;
    }

    @Override
    public void report(
            final AbstractStatusCode status,
            final int line,
            final int column,
            final String... others) {
        hasErrors = true;
        output.error(Status.forLine(status, line, column, others).format());
    }

    @Override
    public boolean hasErrors() {
        return hasErrors;
    }
}
