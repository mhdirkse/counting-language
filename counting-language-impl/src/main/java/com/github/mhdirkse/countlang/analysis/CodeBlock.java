package com.github.mhdirkse.countlang.analysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class CodeBlock {
    private CodeBlock parent;
    private List<CodeBlock> children = new ArrayList<>();
    private Set<CodeBlock> descendants = new HashSet<>();
    private List<VariableWrite> variableWrites = new ArrayList<>();

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
