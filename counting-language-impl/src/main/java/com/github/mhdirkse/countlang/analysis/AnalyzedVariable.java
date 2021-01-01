package com.github.mhdirkse.countlang.analysis;

import java.util.ArrayList;
import java.util.List;

import com.github.mhdirkse.countlang.ast.CountlangType;

import lombok.Getter;

class AnalyzedVariable {
    private final @Getter String name;
    private final @Getter CountlangType countlangType;
    private final @Getter int line;
    private final @Getter int column;

    private final List<VariableEvent> events = new ArrayList<>();

    AnalyzedVariable(String name, CountlangType countlangType, int line, int column, AccessEvent.Type initialAccess) {
        this.name = name;
        this.countlangType = countlangType;
        this.line = line;
        this.column = column;
        AccessEvent event = new AccessEvent(initialAccess, name, line, column);
        events.add(new VariableEvent.Access(event, true));
    }

    void addControlEvent(ControlEvent event) {
        events.add(new VariableEvent.Control(event, false));
    }

    void addAccessEvent(AccessEvent event) {
        events.add(new VariableEvent.Access(event, false));
    }

    int getNumEvents() {
        return events.size();
    }

    VariableEvent getEvent(int i) {
        return events.get(i);
    }
}
