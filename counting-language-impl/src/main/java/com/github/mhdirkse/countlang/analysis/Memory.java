package com.github.mhdirkse.countlang.analysis;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.execution.StackFrameAccess;
import com.github.mhdirkse.countlang.tasks.StatusReporter;

class Memory {
    private AnalysisScopes openScopes = new AnalysisScopes();
    private List<AnalysisScope> allScopes = new ArrayList<>();
    
    private List<AccessEvent> undefinedVariableReads = new ArrayList<>();

    void pushScope(StackFrameAccess access) {
        AnalysisScope scope = new AnalysisScope(access);
        openScopes.push(scope);
        allScopes.add(scope);
    }

    void popScope() {
        openScopes.pop();
    }

    CountlangType read(String name, int line, int column) {
        try {
            return openScopes.read(name, line, column);
        } catch(ReadUndefinedSymbolException e) {
            undefinedVariableReads.add(new AccessEvent(AccessEvent.Type.READ, name, line, column));
            return CountlangType.UNKNOWN;
        }
    }

    void write(String name, CountlangType countlangType, int line, int column) {
        openScopes.write(name, countlangType, line, column);
    }

    void define(String name, CountlangType countlangType, int line, int column) {
        openScopes.define(name, countlangType, line, column);
    }

    void onControlEvent(ControlEvent controlEvent) {
        openScopes.onControlEvent(controlEvent);
    }

    void report(StatusReporter reporter) {
        // TODO: Implement. Report reads of undefined variables and delegate to all scopes.
    }
}
