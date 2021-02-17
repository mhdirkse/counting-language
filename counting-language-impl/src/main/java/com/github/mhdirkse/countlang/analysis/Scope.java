package com.github.mhdirkse.countlang.analysis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.github.mhdirkse.countlang.algorithm.CountlangStackItem;
import com.github.mhdirkse.countlang.algorithm.StackFrameAccess;
import com.github.mhdirkse.countlang.ast.CountlangType;

class Scope implements BlockListener, CountlangStackItem {
    private final StackFrameAccess access;

    private Map<String, Variable> variables = new HashMap<>();

    Scope(StackFrameAccess access) {
        this.access = access;
    }

    @Override
    public StackFrameAccess getAccess() {
        return access;
    }

    /**
     * Enables the default implementations of interface {@link BlockListener}.
     */
    @Override
    public Iterator<Variable> getChildren() {
        return variables.values().iterator();
    }

    @Override
    public boolean has(String name) {
        return variables.containsKey(name);
    }

    CountlangType read(String name, int line, int column, CodeBlock codeBlock) {
        return variables.get(name).read(codeBlock);
    }

    VariableErrorEvent write(String name, int line, int column, CountlangType countlangType, CodeBlock codeBlock) {
        if(variables.containsKey(name)) {
            return variables.get(name).write(line, column, countlangType, codeBlock);
        } else {
            Variable v = new Variable(name, line, column, countlangType, VariableWriteKind.ASSIGNMENT, codeBlock);
            variables.put(v.getName(), v);
            return null;
        }
    }

    void addParameter(String name, int line, int column, CountlangType countlangType, CodeBlock codeBlock) {
        Variable v = new Variable(name, line, column, countlangType, VariableWriteKind.PARAMETER, codeBlock);
        variables.put(v.getName(), v);
    }
}
