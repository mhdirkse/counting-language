/*
 * Copyright Martijn Dirkse 2020
 *
 * This file is part of counting-language.
 *
 * counting-language is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.mhdirkse.countlang.lang.parsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.tree.TerminalNode;

import com.github.mhdirkse.codegen.runtime.HandlerStackContext;
import com.github.mhdirkse.countlang.ast.CountlangType;
import com.github.mhdirkse.countlang.ast.FormalParameter;
import com.github.mhdirkse.countlang.lang.CountlangLexer;
import com.github.mhdirkse.countlang.lang.CountlangParser;

class VarDeclsHandler extends AbstractCountlangListenerHandler {
    private static final Set<Integer> TYPE_TOKENS = new HashSet<>(Arrays.asList(
            CountlangLexer.BOOLTYPE, CountlangLexer.INTTYPE, CountlangLexer.DISTRIBUTIONTYPE));

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
        countlangType = CountlangType.unknown();
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
            countlangType = CountlangType.bool();
        } else if(antlrType == CountlangLexer.INTTYPE) {
            countlangType = CountlangType.integer();
        } else if(antlrType == CountlangLexer.DISTRIBUTIONTYPE) {
            // TODO: Allow distributions of anything, not only int.
            countlangType = CountlangType.distributionOf(CountlangType.integer());
        } else {
            throw new IllegalArgumentException("Unknown type");
        }
    }

    private void handleId(TerminalNode antlrCtx) {
        id = antlrCtx.getText();
    }
}
