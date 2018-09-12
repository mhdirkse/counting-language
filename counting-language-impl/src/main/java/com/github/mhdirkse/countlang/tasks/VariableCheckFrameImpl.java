package com.github.mhdirkse.countlang.tasks;

import java.util.HashMap;
import java.util.Map;

import com.github.mhdirkse.utils.AbstractStatusCode;

class VariableCheckFrameImpl implements VariableCheckFrame {
    private class ReportedVariable {
        final String name;
        final int line;
        final int column;

        ReportedVariable(final String name, final int line, final int column) {
            this.name = name;
            this.line = line;
            this.column = column;
        }

        void reportWith(final AbstractStatusCode statusCode, final StatusReporter reporter) {
            reporter.report(statusCode, line, column, name);
        }
    }

    Map<String, ReportedVariable> undefined = new HashMap<>();
    Map<String, ReportedVariable> notUsed = new HashMap<>();

    @Override
    public void define(final String name, final int line, final int column) {
        notUsed.putIfAbsent(name, new ReportedVariable(name, line, column));
    }

    @Override
    public void use(final String name, final int line, final int column) {
        if(notUsed.containsKey(name)) {
            notUsed.remove(name);
        }
        else {
            undefined.putIfAbsent(name, new ReportedVariable(name, line, column));
        }
    }

    @Override
    public void report(final StatusReporter reporter) {
        undefined.forEach((name, reported) -> reported.reportWith(StatusCode.VAR_UNDEFINED, reporter));
        notUsed.forEach((name, reported) -> reported.reportWith(StatusCode.VAR_NOT_USED, reporter));
    }
}
