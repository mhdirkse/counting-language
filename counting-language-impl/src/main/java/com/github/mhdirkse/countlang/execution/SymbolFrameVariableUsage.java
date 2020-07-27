package com.github.mhdirkse.countlang.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.mhdirkse.countlang.ast.ProgramException;

/**
 * Implementation of SymbolFrame for checking variable usage.
 *
 * An if-statement creates its own block scope, so we assume that no new
 * symbols are introduced in a switch statement.
 * 
 * When both branches of an if-statement write a symbol without reading this,
 * we should not note that the write before the switch is unused. This is because
 * this initial write cannot be omitted in counting-language. As a
 * consequence, this also holds if only one branch overwrites.
 * 
 * If a symbol is read in a branch, then the initial write is used.
 * 
 * if in a branch a symbol is written multiple times without a read in between,
 * then the first write in the branch is unused.
 *
 * @author martijn
 *
 */
public class SymbolFrameVariableUsage implements SymbolFrame<DummyValue> {
    private static enum Usage {
        NEW,
        PARENT,
        USED
    }

    private static class State {
        String name;
        int line;
        int column;
        Usage usage;
        
        State(String name, int line, int column) {
            this.name = name;
            this.line = line;
            this.column = column;
            this.usage = Usage.NEW;
        }

        public State(State other) {
            this.name = other.name;
            this.line = other.line;
            this.column = other.column;
            this.usage = other.usage;
        }
    }

    private static class Delegate {
        private final List<State> eventStore = new ArrayList<>();
        private final Map<String, State> states = new HashMap<>();

        Delegate() {
        }

        Delegate(final Delegate parent) {
            for(String key: parent.states.keySet()) {
                State newState = new State(parent.states.get(key));
                newState.usage = Usage.PARENT;
                states.put(key, newState);
            }
        }

        boolean has(String name) {
            return states.containsKey(name);
        }

        void write(String name, int line, int column) {
            if(states.containsKey(name)) {
                State state = states.get(name);
                if(state.usage == Usage.NEW) {
                    eventStore.add(state);
                }
            }
            states.put(name, new State(name, line, column));
        }

        private DummyValue read(String name, int line, int column) {
            if(!states.containsKey(name)) {
                throw new ProgramException(line, column, "Variable does not exist: " + name);
            }
            State state = states.get(name);
            state.usage = Usage.USED;
            return DummyValue.getInstance();
        }

        List<State> getEventStoreAndCheckNew() {
            for(State s: eventStore) {
                if(s.usage != Usage.NEW) {
                    throw new IllegalStateException(String.format("Cannot come here: %s, %d, %d",
                            s.name, s.line, s.column));
                }
            }
            return eventStore.stream()
                    .map(State::new)
                    .collect(Collectors.toList());
        }
        
        void addEvents(List<State> states) {
            this.eventStore.addAll(states);
        }

        void listEvents(VariableUsageEventHandler handler) {
            List<State> result = getEventStoreAndCheckNew();
            result.addAll(states.values().stream()
                    .filter(s -> s.usage == Usage.NEW)
                    .collect(Collectors.toList()));
            result.forEach(s -> handler.variableNotUsed(s.name, s.line, s.column));
        }
    }

    private final Delegate delegate;

    private boolean switchOpen = false;
    private SymbolFrameVariableUsage branchAnalysis = null;
    
    private final StackFrameAccess access;
    
    SymbolFrameVariableUsage(final StackFrameAccess access) {
        this.access = access;
        delegate = new Delegate();
    }

    private SymbolFrameVariableUsage(SymbolFrameVariableUsage parent) {
        access = parent.access;
        delegate = new Delegate(parent.delegate);
    }
    
    @Override
    public StackFrameAccess getAccess() {
        return access;
    }

    @Override
    public boolean has(String name) {
        return delegate.has(name);
    }

    @Override
    public void write(String name, DummyValue value, int line, int column) {
        if(branchAnalysis != null) {
            if(!has(name)) {
                throw new IllegalStateException("Expected no new symbol to be written in a branch");
            }
            branchAnalysis.write(name, value, line, column);
        } 
        else {
            delegate.write(name, line, column);
        }
    }

    @Override
    public DummyValue read(String name, int line, int column) {
        if(branchAnalysis != null) {
            branchAnalysis.read(name, line, column);
        }
        return delegate.read(name, line, column);
    }

    private List<State> getEventStoreAndCheckNew() {
        return delegate.getEventStoreAndCheckNew();
    }

    void listEvents(final VariableUsageEventHandler handler) {
        delegate.listEvents(handler);
    }

    @Override
    public void onSwitchOpened() {
        if(branchAnalysis == null) {
            switchOpen = true;
        } else {
            branchAnalysis.onSwitchOpened();
        }
    }

    @Override
    public void onSwitchClosed() {
        if(branchAnalysis == null) {
            switchOpen = false;
        } else {
            branchAnalysis.onSwitchClosed();
        }
    }

    @Override
    public void onBranchOpened() {
        if(branchAnalysis == null) {
            branchAnalysis = new SymbolFrameVariableUsage(this);
        } else {
            branchAnalysis.onBranchOpened();
        }
    }

    @Override
    public void onBranchClosed() {
        if(branchAnalysis.switchOpen) {
            branchAnalysis.onBranchClosed();
        } else {
            delegate.addEvents(branchAnalysis.getEventStoreAndCheckNew());
            branchAnalysis = null;
        }
    }
}
