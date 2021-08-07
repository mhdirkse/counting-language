package com.github.mhdirkse.countlang.ast;

import com.github.mhdirkse.countlang.type.CountlangType;

public abstract class TypeNode extends AstNode {
    public TypeNode(int line, int column) {
        super(line, column);
    }

    /** 
     * Assumes that possible children have been type-checked.
     */
    public abstract CountlangType getCountlangType();
}
