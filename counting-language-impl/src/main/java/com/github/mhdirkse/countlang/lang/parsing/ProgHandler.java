package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.Program;
import com.github.mhdirkse.countlang.ast.Statement;

class ProgHandler extends AbstractStatementGroupHandler {
    private Program program;

    Program getProgram() {
        return program;
    }

    ProgHandler(final int line, final int column) {
        program = new Program(line, column);
    }

    @Override
    void addStatement(final Statement statement) {
        program.addStatement(statement);
    }
}
