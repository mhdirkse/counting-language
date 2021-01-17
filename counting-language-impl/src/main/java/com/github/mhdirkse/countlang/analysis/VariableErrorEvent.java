package com.github.mhdirkse.countlang.analysis;

import com.github.mhdirkse.countlang.ast.CountlangType;

import lombok.Getter;

class VariableErrorEvent {
    static enum Kind {
        DOES_NOT_EXIST,
        DUPLICATE_PARAMETER,
        TYPE_MISMATCH;
    }

    private final @Getter Kind kind;
    private final @Getter String name;
    private final @Getter int line;
    private final @Getter int column;
    private final @Getter CountlangType variableType;
    private final @Getter CountlangType typeMismatch;

    VariableErrorEvent(Kind kind, String name, int line, int column) {
        if(kind == Kind.TYPE_MISMATCH) {
            throw new IllegalArgumentException("Do not use this constructor for type mismatches - you need to set the types involved");
        }
        this.kind = kind;
        this.name = name;
        this.line = line;
        this.column = column;
        this.variableType = null;
        this.typeMismatch = null;
    }

    VariableErrorEvent(String name, int line, int column, CountlangType variableType, CountlangType typeMismatch) {
        this.kind = Kind.TYPE_MISMATCH;
        this.name = name;
        this.line = line;
        this.column = column;
        this.variableType = variableType;
        this.typeMismatch = typeMismatch;
    }
}
