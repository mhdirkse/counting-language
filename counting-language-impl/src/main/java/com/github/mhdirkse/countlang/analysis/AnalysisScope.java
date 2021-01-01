package com.github.mhdirkse.countlang.analysis;

import java.util.HashMap;
import java.util.Map;

import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.execution.StackFrameAccess;

class AnalysisScope {
    private final StackFrameAccess access;
    private final Map<String, AnalyzedVariable> variables = new HashMap<>();

    AnalysisScope(StackFrameAccess access) {
        this.access = access;
    }

    StackFrameAccess getAccess() {
        return access;
    }

    boolean has(String symbolName) {
        return variables.containsKey(symbolName);
    }

    /**
     * Assumes that the variable exists.
     */
    CountlangType read(String name, int line, int column) {
        AnalyzedVariable variable = variables.get(name);
        variable.addAccessEvent(new AccessEvent(AccessEvent.Type.READ, name, line, column));
        return variable.getCountlangType();
    }

    void write(String name, CountlangType countlangType, int line, int column) {
        if(variables.containsKey(name)) {
            AnalyzedVariable variable = variables.get(name);
            variable.addAccessEvent(new AccessEvent(AccessEvent.Type.WRITE, name, line, column));
        } else {
            variables.put(name, new AnalyzedVariable(name, countlangType, line, column, AccessEvent.Type.WRITE));
        }
    }

    void define(String name, CountlangType countlangType, int line, int column) {
        if(variables.containsKey(name)) {
            // TODO: Error variable already defined
        } else {
            variables.put(name, new AnalyzedVariable(name, countlangType, line, column, AccessEvent.Type.DEFINE));
        }
    }

    void onControlEvent(ControlEvent ev) {
        variables.values().forEach(v -> v.addControlEvent(ev));
    }
}
