package com.github.mhdirkse.countlang.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.github.mhdirkse.countlang.algorithm.StackFrameAccess;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.utils.Stack;

class MemoryImpl implements Memory {
    private Stack<Scope> scopes = new Stack<>();
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
        Scope scope = findScope(name);
        if(scope.hasVariable(name)) {
            return scope.read(name, line, column, codeBlock);
        } else {
            variableErrorEvents.add(new VariableErrorEvent(VariableErrorEvent.Kind.DOES_NOT_EXIST, name, line, column));
            return CountlangType.UNKNOWN;
        }
    }

    @Override
    public void write(String name, int line, int column, CountlangType countlangType, CodeBlock codeBlock) {
        VariableErrorEvent optionalTypeMismatch = findScope(name).write(name, line, column, countlangType, codeBlock);
        if(optionalTypeMismatch != null) {
            variableErrorEvents.add(optionalTypeMismatch);
        }
    }

    @Override
    public void addParameter(String name, int line, int column, CountlangType countlangType, CodeBlock codeBlock) {
        Scope scope = findScope(name);
        if(scope.hasVariable(name)) {
            variableErrorEvents.add(new VariableErrorEvent(VariableErrorEvent.Kind.DUPLICATE_PARAMETER, name, line, column));
        } else {
            scope.addParameter(name, line, column, countlangType, codeBlock);
        }
    }

    private Scope findScope(String name) {
        Iterator<Scope> it = scopes.topToBottomIterator();
        while(it.hasNext()) {
            Scope candidate = it.next();
            if(candidate.hasVariable(name)) {
                return candidate;
            }
            if(candidate.getAccess() == StackFrameAccess.HIDE_PARENT) {
                break;
            }
        }
        return scopes.peek();
    }
}
