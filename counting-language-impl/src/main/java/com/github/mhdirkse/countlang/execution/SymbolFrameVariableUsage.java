/*
 * Copyright Martijn Dirkse 2020
 *
 * This file is part of counting-language.
 *
 * counting-language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.mhdirkse.countlang.execution;

import com.github.mhdirkse.countlang.utils.Stack;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.mhdirkse.countlang.ast.ProgramException;

/**
 * Implementation of SymbolFrame for checking variable usage.
 * <p>
 * Without branching, variable usage checking would be straightforward.
 * Every write of a symbol should be followed by a read. If a write
 * is followed by another write, then the first write is reported
 * as unused. We can assume that a symbol is not read before it is
 * written, because that would be caught by type checking.
 * <p>
 * A symbol can be written and read multiple times during the life
 * time of the symbol frame. We have to report all occasions that
 * a write was not read. Therefore an event store is introduced.
 * When a symbol write is followed by a symbol write, then the
 * first write is added to the event store.
 * <p>
 * This behavior is implemented into the nested class {@link Delegate}.
 * <p>
 * The behavior of the outer class is more complicated because of branching.
 * An if-statement creates its own block scope, so we assume that no new
 * symbols are introduced in a switch statement. When both branches
 * of an if-statement write a symbol without reading it,
 * we should not note that the write before the switch is unused.
 * This is because this initial write cannot be omitted in
 * counting-language. As a consequence, this also holds if
 * only one branch overwrites. If a symbol is read in a branch,
 * then the initial write is used.
 * <p>
 * if in a branch a symbol is written multiple times without a read in between,
 * then the first write in the branch is unused. This introduces recursion.
 * When there is a branch, then the unused writes in the context of the
 * branch are observed by a copy of the original {@link SymbolFrameVariableUsage}.
 * Managing this recursion is the responsibility of the outer class.
 *
 * @author martijn
 *
 */
class SymbolFrameVariableUsage implements SymbolFrame<DummyValue> {
    private static class State {
        final String name;
        final int line;
        final int column;
        
        State(String name, int line, int column) {
            this.name = name;
            this.line = line;
            this.column = column;
        }
    }

    private static class WhileContext {
        private final Set<String> circularUsageCandidates;
        private final Set<String> circularUsedVariables = new HashSet<>();

        WhileContext(SymbolFrameVariableUsage subject) {
            circularUsageCandidates = new HashSet<>();
            circularUsageCandidates.addAll(subject.newWrites.keySet());
            circularUsageCandidates.addAll(subject.writesNotRequiringRead.keySet());
        }

        void write(String name) {
            circularUsageCandidates.remove(name);
        }

        void read(String name) {
            if(circularUsageCandidates.contains(name)) {
                circularUsedVariables.add(name);
            }
            circularUsageCandidates.remove(name);
        }
    }

    private final List<State> overwrittenWrites = new ArrayList<>();
    private final Map<String, State> newWrites = new HashMap<>();
    private final Map<String, State> writesNotRequiringRead = new HashMap<>();

    private Stack<WhileContext> whileContexts = new Stack<>();
    private boolean switchOpen = false;
    private SymbolFrameVariableUsage branchAnalysis = null;
    
    private final StackFrameAccess access;
    
    SymbolFrameVariableUsage(final StackFrameAccess access) {
        this.access = access;
    }

    private SymbolFrameVariableUsage(SymbolFrameVariableUsage parent) {
        access = parent.access;
        writesNotRequiringRead.putAll(parent.newWrites);
        writesNotRequiringRead.putAll(parent.writesNotRequiringRead);
    }
    
    @Override
    public StackFrameAccess getAccess() {
        return access;
    }

    @Override
    public boolean has(String name) {
        return newWrites.containsKey(name) || writesNotRequiringRead.containsKey(name);
    }

    @Override
    public void write(String name, DummyValue value, int line, int column) {
        if((branchAnalysis != null) && (! has(name))) {
            throw new IllegalStateException("Expected no new symbol to be written in a branch");
        }
        writeUnchecked(name, value, line, column);
    }

    public void writeUnchecked(String name, DummyValue value, int line, int column) {
        whileContexts.forEach(wc -> wc.write(name));
        if(branchAnalysis != null) {
            branchAnalysis.write(name, value, line, column);
        } 
        else {
            handleExistingWrite(name);
            newWrites.put(name, new State(name, line, column));
        }
    }

    private void handleExistingWrite(String name) {
        if(newWrites.containsKey(name)) {
            markWriteOverwritten(name);
        }
        writesNotRequiringRead.remove(name);
    }

    private void markWriteOverwritten(String name) {
        State state = newWrites.remove(name);
        overwrittenWrites.add(state);
    }

    @Override
    public DummyValue read(String name, int line, int column) {
        if(!has(name)) {
            throw new ProgramException(line, column, "Variable does not exist: " + name);
        }
        return readUnchecked(name, line, column);
    }

    public DummyValue readUnchecked(String name, int line, int column) {
        whileContexts.forEach(wc -> wc.read(name));
        if(branchAnalysis != null) {
            branchAnalysis.read(name, line, column);
        }
        markWriteUsed(name);
        return DummyValue.getInstance();
    }

    private void markWriteUsed(String name) {
        if(newWrites.containsKey(name)) {
            State state = newWrites.remove(name);
            writesNotRequiringRead.put(name, state);            
        }
    }

    void listEvents(final VariableUsageEventHandler handler) {
        List<State> result = new ArrayList<>();
        result.addAll(overwrittenWrites);
        result.addAll(newWrites.values());
        result.forEach(s -> handler.variableNotUsed(s.name, s.line, s.column));
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
            overwrittenWrites.addAll(branchAnalysis.overwrittenWrites);
            branchAnalysis = null;
        }
    }

    @Override
    public void onRepetitionOpened() {
        if(branchAnalysis == null) {
            whileContexts.push(new WhileContext(this));
        } else {
            branchAnalysis.onRepetitionOpened();
        }
    }

    @Override
    public void onRepetitionClosed() {
        if(branchAnalysis == null) {
            Set<String> circularUsed = whileContexts.pop().circularUsedVariables;
            circularUsed.forEach(this::markWriteUsed);
        } else {
            branchAnalysis.onRepetitionClosed();
        }
    }
}
