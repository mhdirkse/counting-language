package com.github.mhdirkse.countlang.lang.parsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.execution.CountlangType;
import com.github.mhdirkse.countlang.lang.CountlangLexer;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class VarDeclsHandler extends AbstractCountlangListenerHandler {
    private static final Set<Integer> TYPE_TOKENS = new HashSet<>(Arrays.asList(
            CountlangLexer.BOOLTYPE, CountlangLexer.INTTYPE));

    private int line;
    private int column;
    private CountlangType countlangType;
    private String id;

    VarDeclsHandler() {
        super(false);
    }

    private List<FormalParameter> formalParameters = new ArrayList<>();

    List<FormalParameter> getFormalParameters() {
        return formalParameters;
    }

    @Override
    public boolean enterVarDecl(
            CountlangParser.VarDeclContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        line = antlrCtx.start.getLine();
        column = antlrCtx.start.getCharPositionInLine();
        countlangType = CountlangType.UNKNOWN;
        id = "";
        return true;
    }

    @Override
    public boolean exitVarDecl(
            CountlangParser.VarDeclContext antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        formalParameters.add(new FormalParameter(line, column, id, countlangType));
        return true;
    }

    @Override
    public boolean visitTerminal(
            TerminalNode antlrCtx,
            HandlerStackContext<CountlangListenerHandler> delegationCtx) {
        if(antlrCtx.getSymbol().getType() == CountlangLexer.ID) {
            handleId(antlrCtx);
            return true;
        } else if(TYPE_TOKENS.contains(antlrCtx.getSymbol().getType())) {
            handleType(antlrCtx);
            return true;
        } else {
            return false;
        }
    }

    private void handleType(TerminalNode antlrCtx) {
        int antlrType = antlrCtx.getSymbol().getType();
        if(antlrType == CountlangLexer.BOOLTYPE) {
            countlangType = CountlangType.BOOL;
        } else if(antlrType == CountlangLexer.INTTYPE) {
            countlangType = CountlangType.INT;
        } else {
            throw new IllegalArgumentException("Unknown type");
        }
    }

    private void handleId(TerminalNode antlrCtx) {
        id = antlrCtx.getText();
    }
}
