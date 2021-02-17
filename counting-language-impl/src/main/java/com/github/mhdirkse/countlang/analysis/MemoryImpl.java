package com.github.mhdirkse.countlang.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.github.mhdirkse.countlang.algorithm.CountlangStack;
import com.github.mhdirkse.countlang.ast.CountlangType;

class MemoryImpl implements Memory {
    private CountlangStack<Scope> scopes = new CountlangStack<Scope>();
    private List<VariableErrorEvent> variableErrorEvents = new ArrayList<>();

    @Override
    public List<VariableErrorEvent> getVariableErrorEvents() {
        return Collections.unmodifiableList(variableErrorEvents);
    }

    @Override
    public void pushScope(Scope scope) {
        scopes.push(scope);
    }

    @Override
    public Scope popScope() {
        return scopes.pop();
    }

    @Override
    public boolean isAtRootScope() {
        return scopes.size() == 1;
    }

    /**
     * Enables default implementations of interface {@link BlockListener}.
     */
    @Override
    public Iterator<Scope> getChildren() {
        return scopes.topToBottomIterator();
    }

    @Override
    public CountlangType read(String name, int line, int column, CodeBlock codeBlock) {
        Scope scope = scopes.findFrame(name);
        if(scope.has(name)) {
            return scope.read(name, line, column, codeBlock);
        } else {
            variableErrorEvents.add(new VariableErrorEvent(VariableErrorEvent.Kind.DOES_NOT_EXIST, name, line, column));
            return CountlangType.UNKNOWN;
        }
    }

    @Override
    public void write(String name, int line, int column, CountlangType countlangType, CodeBlock codeBlock) {
        VariableErrorEvent optionalTypeMismatch = scopes.findFrame(name).write(name, line, column, countlangType, codeBlock);
        if(optionalTypeMismatch != null) {
            variableErrorEvents.add(optionalTypeMismatch);
        }
    }

    @Override
    public void addParameter(String name, int line, int column, CountlangType countlangType, CodeBlock codeBlock) {
        Scope scope = scopes.findFrame(name);
        if(scope.has(name)) {
            variableErrorEvents.add(new VariableErrorEvent(VariableErrorEvent.Kind.DUPLICATE_PARAMETER, name, line, column));
        } else {
            scope.addParameter(name, line, column, countlangType, codeBlock);
        }
    }
}
