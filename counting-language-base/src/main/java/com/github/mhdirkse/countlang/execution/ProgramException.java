package com.github.mhdirkse.countlang.execution;

import com.github.mhdirkse.countlang.utils.Utils;

public class ProgramException extends RuntimeException {
    private static final long serialVersionUID = -665914712141851344L;

    private final int line;
    private final int column;
    private final String simpleMessage;

    public ProgramException(
            int line,
            int column,
            String simpleMessage) {
        this.line = line;
        this.column = column;
        this.simpleMessage = simpleMessage;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getSimpleMessage() {
        return simpleMessage;
    }
 
    @Override
    public String getMessage() {
        return Utils.formatLineColumnMessage(line, column, simpleMessage);
    }
}