package com.github.mhdirkse.countlang.analysis;

import java.util.HashSet;
import java.util.Set;

import com.github.mhdirkse.countlang.ast.CountlangType;

import lombok.Getter;

class VariableWrite {
    private final @Getter Variable variable;
    private final @Getter int line;
    private final @Getter int column;
    private final @Getter CountlangType countlangType;
    private final @Getter VariableWriteKind variableWriteKind;
    private final @Getter CodeBlock codeBlock;
    private final @Getter boolean initial;

    private final Set<CodeBlock> readBy = new HashSet<>();
    private final Set<CodeBlock> overwrittenBy = new HashSet<>();

    VariableWrite(Variable variable, int line, int column, CountlangType countlangType, VariableWriteKind variableWriteKind, CodeBlock codeBlock, boolean initial) {
        this.variable = variable;
        this.line = line;
        this.column = column;
        this.countlangType = countlangType;
        this.variableWriteKind = variableWriteKind;
        this.codeBlock = codeBlock;
        this.initial = initial;
    }

    void read(CodeBlock codeBlock) {
        readBy.add(codeBlock);
    }

    void overwrite(VariableWrite overwritingWrite) {
        overwrittenBy.add(overwritingWrite.getCodeBlock());
    }

    boolean isRead() {
        return ! readBy.isEmpty();
    }

    boolean isOverwritten() {
        return ! overwrittenBy.isEmpty();
    }
}
