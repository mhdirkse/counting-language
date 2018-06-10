package com.github.mhdirkse.countlang.lang.parsing;

import com.github.mhdirkse.countlang.ast.Program;
import com.github.mhdirkse.countlang.ast.Statement;

class ProgHandler2 extends AbstractStatementGroupHandler2 {
    private Program program;

    Program getProgram() {
        return program;
    }

    ProgHandler2(final int line, final int column) {
        program = new Program(line, column);
    }

    @Override
    void addStatement(final Statement statement) {
        program.addStatement(statement);
    }
}
