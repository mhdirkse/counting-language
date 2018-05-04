package com.github.mhdirkse.countlang.lang;

import org.antlr.v4.runtime.misc.NotNull;

import com.github.mhdirkse.countlang.ast.Program;

class RootListener extends AbstractListener {
    private Program program = null;

    public Program getProgram() {
        return program;
    }

    @Override
    public void enterProgImpl(@NotNull CountlangParser.ProgContext ctx) {
        int line = ctx.start.getLine();
        int column = ctx.start.getCharPositionInLine();
        delegate = new ProgListener(line, column);
    }

    @Override
    public void visitProgram(final Program program) {
        this.program = program;
        delegate = null;
    }
}
