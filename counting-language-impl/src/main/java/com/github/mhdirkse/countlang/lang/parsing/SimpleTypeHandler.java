package com.github.mhdirkse.countlang.lang.parsing;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.lang.CountlangLexer;

class SimpleTypeHandler extends AbstractTypeHandler {
    private CountlangType countlangType;

    SimpleTypeHandler(int line, int column) {
        super(line, column);
    }

    @Override
    public CountlangType getCountlangType() {
        return countlangType;
    }

    @Override
    public boolean visitTerminal(
            TerminalNode antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        int antlrType = antlrCtx.getSymbol().getType();
        if(antlrType == CountlangLexer.BOOLTYPE) {
            countlangType = CountlangType.bool();
        } else if(antlrType == CountlangLexer.INTTYPE) {
            countlangType = CountlangType.integer();
        } else {
            throw new IllegalArgumentException("Unknown type");
        }
        return true;
    }
}
