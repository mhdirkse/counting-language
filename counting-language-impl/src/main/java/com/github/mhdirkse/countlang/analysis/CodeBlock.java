package com.github.mhdirkse.countlang.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class CodeBlock {
    private CodeBlock parent;
    private final List<CodeBlock> children = new ArrayList<>();

    List<CodeBlock> getChildren() {
        return Collections.unmodifiableList(children);
    }

    private final Set<CodeBlock> descendants = new HashSet<>();

    Set<CodeBlock> getDescendants() {
        return Collections.unmodifiableSet(descendants);
    }

    private final List<VariableWrite> variableWrites = new ArrayList<>();

    List<VariableWrite> getVariableWrites() {
        return Collections.unmodifiableList(variableWrites);
    }

    CodeBlock() {
        parent = null;
    }

    CodeBlock createChild() {
        CodeBlock result = new CodeBlock(this);
        children.add(result);
        this.addDescendant(result);
        return result;
    }

    private CodeBlock(CodeBlock parent) {
        this.parent = parent;
    }

    private void addDescendant(CodeBlock descendant) {
        descendants.add(descendant);
        if(parent != null) {
            parent.addDescendant(descendant);
        }
    }

    void addVariableWrite(VariableWrite variableWrite) {
        variableWrites.add(variableWrite);
    }

    boolean contains(CodeBlock other) {
        return descendants.contains(other);
    }
}
