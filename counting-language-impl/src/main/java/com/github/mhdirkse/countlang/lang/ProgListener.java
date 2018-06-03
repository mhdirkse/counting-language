package com.github.mhdirkse.countlang.lang;

import org.antlr.v4.runtime.misc.NotNull;

import com.github.mhdirkse.countlang.ast.AstNode;
import com.github.mhdirkse.countlang.ast.Program;
import com.github.mhdirkse.countlang.ast.Statement;

class ProgListener extends AbstractStatementListener {
    private final Program program;
    private final AstNode.Visitor parent;

    ProgListener(final int line, final int column, final AstNode.Visitor parent) {
        program = new Program(line, column);
        this.parent = parent;
    }

    @Override
    void handleStatement(final Statement statement) {
        program.addStatement(statement);
    }

    @Override
    public void exitProgImpl(@NotNull CountlangParser.ProgContext ctx) {
        parent.visitProgram(program);
    }
}
