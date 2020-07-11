package com.github.mhdirkse.countlang.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.mhdirkse.countlang.ast.ProgramException;
import com.github.mhdirkse.countlang.ast.StackFrameAccess;

public class SymbolFrameVariableUsage implements SymbolFrame<DummyValue>{
    class State {
        String name;
        int line;
        int column;
        boolean isUsed;
        
        State(String name, int line, int column) {
            this.name = name;
            this.line = line;
            this.column = column;
            this.isUsed = false;
        }
    }

    private final List<State> unusedOverwritten = new ArrayList<>();
    private final Map<String, State> states = new HashMap<>();

    private final StackFrameAccess access;
    
    SymbolFrameVariableUsage(final StackFrameAccess access) {
        this.access = access;
    }

    @Override
    public StackFrameAccess getAccess() {
        return access;
    }

    @Override
    public boolean has(String name) {
        return states.containsKey(name);
    }

    @Override
    public <V extends DummyValue> void write(String name, V value, int line, int column) {
        if(states.containsKey(name)) {
            State state = states.get(name);
            if(!state.isUsed) {
                unusedOverwritten.add(state);
            }
        }
        states.put(name, new State(name, line, column));
    }

    @Override
    public DummyValue read(String name, int line, int column) {
        if(!states.containsKey(name)) {
            throw new ProgramException(line, column, "Variable does not exist: " + name);
        }
        State state = states.get(name);
        state.isUsed = true;
        return DummyValue.getInstance();
    }

    void listEvents(VariableUsageEventHandler handler) {
        for(State s: unusedOverwritten) {
            if(s.isUsed) {
                throw new IllegalStateException(String.format("Cannot come here: %s, %d, %d",
                        s.name, s.line, s.column));
            }
            handler.variableNotUsed(s.name, s.line, s.column);
        }
        for(State s: states.values()) {
            if(!s.isUsed) {
                handler.variableNotUsed(s.name, s.line, s.column);
            }
        }
    }
}
