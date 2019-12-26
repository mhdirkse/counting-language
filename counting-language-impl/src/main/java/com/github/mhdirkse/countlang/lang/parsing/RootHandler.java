package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.misc.NotNull;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.Program;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class RootHandler extends AbstractCountlangListenerHandler {
    private Program program = null;

    RootHandler() {
        super(false);
    }

    Program getProgram() {
        return program;
    }

    @Override
    public boolean enterProg(
            @NotNull CountlangParser.ProgContext antlrCtx, final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int line = antlrCtx.start.getLine();
        int column = antlrCtx.start.getCharPositionInLine();
        delegationCtx.addFirst(new ProgHandler(line, column));
        return true;
    }

    @Override
    public boolean exitProg(
            @NotNull CountlangParser.ProgContext antlrCtx, final HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        program = ((ProgHandler) delegationCtx.getPreviousHandler()).getProgram();
        delegationCtx.removeAllPreceeding();
        return true;
    }
}
