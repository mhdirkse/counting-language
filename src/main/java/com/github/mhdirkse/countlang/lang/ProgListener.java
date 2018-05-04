package com.github.mhdirkse.countlang.lang;

import com.github.mhdirkse.countlang.ast.Program;

class ProgListener extends AbstractListener {
    private final Program program;

    ProgListener(final int line, final int column) {
        program = new Program(line, column);
    }
}
