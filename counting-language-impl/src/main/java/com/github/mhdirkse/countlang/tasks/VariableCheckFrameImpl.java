package com.github.mhdirkse.countlang.tasks;

import java.util.HashMap;
import java.util.Map;

import com.github.mhdirkse.utils.AbstractStatusCode;

class VariableCheckFrameImpl implements VariableCheckFrame {
    private class ReportedVariable {
        String name;
        int line;
        int column;

        ReportedVariable(final String name, final int line, final int column) {
            this.name = name;
            this.line = line;
            this.column = column;
        }

        ReportedVariable(final ReportedVariable other) {
            this.name = other.name;
            this.line = other.line;
            this.column = other.column;
        }

        void reportWith(final AbstractStatusCode statusCode, final StatusReporter reporter) {
            reporter.report(statusCode, line, column, name);
        }
    }

    private enum VariableStateCode {
        UNDEFINED,
        DEFINED,
        USED
    }

    private class VariableState extends ReportedVariable {
        VariableStateCode variableState;

        VariableState(
                final String name,
                final int line,
                final int column,
                final VariableStateCode variableState) {
            super(name, line, column);
            this.variableState = variableState;
        }
    }

    Map<String, VariableState> variableStates = new HashMap<>();
    Map<String, ReportedVariable> undefined = new HashMap<>();
    Map<String, ReportedVariable> notUsed = new HashMap<>();

    @Override
    public void define(final String name, final int line, final int column) {
        if(variableStates.containsKey(name)) {
            onRedefined(name, line, column);
        } else {
            variableStates.put(name, new VariableState(name, line, column, VariableStateCode.DEFINED));
        }
    }

    private void onRedefined(final String name, final int line, final int column) {
        VariableState oldState = variableStates.get(name);
        switch(oldState.variableState) {
        case DEFINED:
            notUsed.putIfAbsent(name, new ReportedVariable(oldState));
            break;
        case USED:
        case UNDEFINED:
            variableStates.put(name, new VariableState(name, line, column, VariableStateCode.DEFINED));
            break;
        }
    }

    @Override
    public void use(final String name, final int line, final int column) {
        if(variableStates.containsKey(name)) {
            onReused(name, line, column);
        } else {
            variableStates.put(name, new VariableState(name, line, column, VariableStateCode.UNDEFINED));
            undefined.putIfAbsent(name, new ReportedVariable(name, line, column));
        }
    }

    private void onReused(final String name, final int line, final int column) {
        VariableState oldState = variableStates.get(name);
        switch(oldState.variableState) {
        case DEFINED:
            variableStates.get(name).variableState = VariableStateCode.USED;
            break;
        case USED:
        case UNDEFINED:
            break;
        }
    }

    @Override
    public void report(final StatusReporter reporter) {
        variableStates.values().stream()
            .filter(vs -> (vs.variableState == VariableStateCode.DEFINED))
            .forEach(vs -> notUsed.putIfAbsent(vs.name, new ReportedVariable(vs)));
        undefined.forEach((name, reported) -> reported.reportWith(StatusCode.VAR_UNDEFINED, reporter));
        notUsed.forEach((name, reported) -> reported.reportWith(StatusCode.VAR_NOT_USED, reporter));
    }
}
