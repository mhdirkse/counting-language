package com.github.mhdirkse.countlang.tasks;

import java.util.HashMap;
import java.util.Map;

import com.github.mhdirkse.countlang.ast.StackFrameAccess;
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
        NEW,
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

    private final StackFrameAccess stackFrameAccess;
    
    @Override
    public StackFrameAccess getStackFrameAccess() {
        return stackFrameAccess;
    }

    VariableCheckFrameImpl(final StackFrameAccess stackFrameAccess) {
        this.stackFrameAccess = stackFrameAccess;
    }
    
    @Override
    public void define(final String name, final int line, final int column) {
        variableStates.putIfAbsent(name, new VariableState(name, line, column, VariableStateCode.NEW));
        VariableState currentState = variableStates.get(name);
        doDefine(name, line, column, currentState);
    }

    private void doDefine(final String name, final int line, final int column, VariableState currentState) {
        if(currentState.variableState == VariableStateCode.DEFINED) {
            notUsed.putIfAbsent(name, new ReportedVariable(currentState));
        }
        variableStates.put(name, new VariableState(name, line, column, VariableStateCode.DEFINED));
    }

    @Override
    public void use(final String name, final int line, final int column) {
        if(variableStates.containsKey(name)) {
            variableStates.get(name).variableState = VariableStateCode.USED;
        } else {
            undefined.putIfAbsent(name, new ReportedVariable(name, line, column));
        }
    }

    @Override
    public boolean hasSymbol(final String name) {
        return variableStates.containsKey(name);
    }
    
    @Override
    public void report(final StatusReporter reporter) {
        finishNotUsed();
        undefined.forEach((name, reported) -> reported.reportWith(StatusCode.VAR_UNDEFINED, reporter));
        notUsed.forEach((name, reported) -> reported.reportWith(StatusCode.VAR_NOT_USED, reporter));
    }

    private void finishNotUsed() {
        variableStates.values().stream()
            .filter(vs -> (vs.variableState == VariableStateCode.DEFINED))
            .forEach(vs -> notUsed.putIfAbsent(vs.name, new ReportedVariable(vs)));
    }
}
