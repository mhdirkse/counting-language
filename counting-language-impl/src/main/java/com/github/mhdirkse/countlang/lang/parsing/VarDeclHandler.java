package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.lang.CountlangLexer;

class VarDeclHandler extends TypeIdHandler {
    private String id;

    VarDeclHandler(int line, int column) {
        super(line, column);
    }
   
    String getId() {
        return id;
    }

    @Override
    public boolean visitTerminal(
            TerminalNode antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        if(antlrCtx.getSymbol().getType() == CountlangLexer.ID) {
            id = antlrCtx.getText();
            return true;
        } else {
            return false;
        }
    }
}
